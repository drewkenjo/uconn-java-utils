package uconn.utils.pid.stefan;

import org.jlab.clas.physics.LorentzVector;
import org.jlab.detector.base.DetectorType;
import java.util.stream.IntStream;
import org.jlab.jnp.hipo4.data.Bank;
import uconn.utils.pid.Candidate;

public class KaonCandidate extends Candidate {

    /// This is the enum for Kaon cut types
    public enum Cut {
        PID, ///< cut on PDG code
        CHI2PID_CUT, ///< cut on chi2pid
        DC_FIDUCIAL_REG1, ///< fiducial DC cut for region 1
        DC_FIDUCIAL_REG2, ///< fiducial DC cut for region 2
        DC_FIDUCIAL_REG3, ///< fiducial DC cut for region 3
        FORWARD, ///< only forward detector
        DELTA_VZ, ///< cut on difference between VZ of Kaon candidate and trigger particle
    }


    /** A Constructor
     * @param ipart particle index
     */
    public KaonCandidate(int ipart) {
        super(ipart);
    }



    /**
     * return KaonCandidate instance
     * @param ipart particle index
     * @param recbank,trajbank particle and trajectory banks
     * @param isinbending true for inbending, false for outbending
     */
    public static KaonCandidate getKaonCandidate(int ipart, Bank recbank, Bank trajbank, boolean isinbending) {
        KaonCandidate candidate = new KaonCandidate(ipart);
        if(!isinbending) candidate.setOUTBENDING();

        if(recbank!=null) {
            candidate.setPID(recbank.getInt("pid",ipart));
            candidate.setStatus(recbank.getShort("status",ipart));
            candidate.setCHI2PID(recbank.getFloat("chi2pid",ipart));
            candidate.setVZ(recbank.getFloat("vz",ipart));
            candidate.setDVZ(recbank.getFloat("vz",ipart) - recbank.getFloat("vz",0));
            candidate.setPxyz(recbank.getFloat("px",ipart), recbank.getFloat("py",ipart), recbank.getFloat("pz",ipart));
        }

        if(trajbank!=null) IntStream.range(0,trajbank.getRows())
            .filter(i -> trajbank.getShort("pindex",i) == ipart && trajbank.getByte("detector",i) == DetectorType.DC.getDetectorId())
            .forEach(i -> {
            if(trajbank.getByte("layer",i) == 6)
                candidate.setDCxyz(1, trajbank.getFloat("x",i), trajbank.getFloat("y",i), trajbank.getFloat("z",i));
            else if(trajbank.getByte("layer",i) == 18)
                candidate.setDCxyz(2, trajbank.getFloat("x",i), trajbank.getFloat("y",i), trajbank.getFloat("z",i));
            else if(trajbank.getByte("layer",i) == 36)
                candidate.setDCxyz(3, trajbank.getFloat("x",i), trajbank.getFloat("y",i), trajbank.getFloat("z",i));
        });

        return candidate;
    }



    /**
     * @return LorentzVector instance
     */
    public LorentzVector getLorentzVector() {
        LorentzVector vec = null;
        if(px!=null && py!=null && pz!=null) {
            vec = new LorentzVector();
            vec.setPxPyPzM(px,py,pz,0.493677);
        }
        return vec;
    }


    /**
     * @return if it is detected in forward
     */
    public boolean cut_FORWARD() {
        if(status==null) return false;
        return status>=2000 && status<4000;
    }


    /**
     * @return chi2pid cut
     */
    public boolean cut_CHI2PID() {
        if(pid==null || chi2pid==null || p==null) return false;
        return Math.abs(chi2pid)<3;
    }


    /**
     * @return DC fiducial region 1
     */
    public boolean cut_DC_FIDUCIAL_REG1() {
        if(dc_sector==null || traj_x1==null || traj_y1==null || traj_z1==null || pid==null) return false;
        if(field==MagField.INBENDING)
          return HadronCuts.DC_fiducial_cut_theta_phi(dc_sector, 1, traj_x1, traj_y1, traj_z1, pid, field==MagField.INBENDING);
        return ElectronCuts.DC_fiducial_cut_XY(dc_sector, 1, traj_x1, traj_y1, pid, field==MagField.INBENDING);
    }


    /**
     * @return DC fiducial region 2
     */
    public boolean cut_DC_FIDUCIAL_REG2() {
        if(dc_sector==null || traj_x2==null || traj_y2==null || traj_z2==null || pid==null) return false;
        if(field==MagField.INBENDING)
          return HadronCuts.DC_fiducial_cut_theta_phi(dc_sector, 2, traj_x2, traj_y2, traj_z2, pid, field==MagField.INBENDING);
        return ElectronCuts.DC_fiducial_cut_XY(dc_sector, 2, traj_x1, traj_y1, pid, field==MagField.INBENDING);
    }


    /**
     * @return DC fiducial region 3
     */
    public boolean cut_DC_FIDUCIAL_REG3() {
        if(dc_sector==null || traj_x3==null || traj_y3==null || traj_z3==null || pid==null) return false;
        if(field==MagField.INBENDING)
          return HadronCuts.DC_fiducial_cut_theta_phi(dc_sector, 3, traj_x3, traj_y3, traj_z3, pid, field==MagField.INBENDING);
        return ElectronCuts.DC_fiducial_cut_XY(dc_sector, 3, traj_x1, traj_y1, pid, field==MagField.INBENDING);
    }


    /**
     * @return delta vz cut
     */
    public boolean cut_DELTA_VZ() {
        if(pid==null || dvz==null) return false;
        return HadronCuts.Delta_vz_cut(pid, dvz);
    }


    /**
     * testing against all K- cuts
     */
    public boolean isKm() {
        return isKm(Cut.values());
    }



    /**
     * assembly of multiple K- cuts
     * @param applycuts the list of cuts required to apply
     */
    public boolean isKm(Cut ...applycuts) {
        for(Cut thiscut: applycuts) {
            if(thiscut == Cut.PID) {
                if(pid == null) return false;
                else if(pid != -321) return false;

            } else if(thiscut == Cut.FORWARD) {
                if(!cut_FORWARD()) return false;

            } else if(thiscut == Cut.CHI2PID_CUT) {
                 if(!cut_CHI2PID()) return false;

            } else if(thiscut == Cut.DC_FIDUCIAL_REG1) {
                 if(!cut_DC_FIDUCIAL_REG1()) return false;

            } else if(thiscut == Cut.DC_FIDUCIAL_REG2) {
                 if(!cut_DC_FIDUCIAL_REG2()) return false;

            } else if(thiscut == Cut.DC_FIDUCIAL_REG3) {
                 if(!cut_DC_FIDUCIAL_REG3()) return false;

            } else if(thiscut == Cut.DELTA_VZ) {
                 if(!cut_DELTA_VZ()) return false;

            } else {
                return false;
            }
        }
        return true;
    }



    /**
     * testing against all K+ cuts
     */
    public boolean isKp() {
        return isKp(Cut.values());
    }



    /**
     * assembly of multiple K+ cuts
     * @param applycuts the list of cuts required to apply
     */
    public boolean isKp(Cut ...applycuts) {
        for(Cut thiscut: applycuts) {
            if(thiscut == Cut.PID) {
                if(pid == null) return false;
                else if(pid != 321) return false;

            } else if(thiscut == Cut.FORWARD) {
                if(!cut_FORWARD()) return false;

            } else if(thiscut == Cut.CHI2PID_CUT) {
                 if(!cut_CHI2PID()) return false;

            } else if(thiscut == Cut.DC_FIDUCIAL_REG1) {
                 if(!cut_DC_FIDUCIAL_REG1()) return false;

            } else if(thiscut == Cut.DC_FIDUCIAL_REG2) {
                 if(!cut_DC_FIDUCIAL_REG2()) return false;

            } else if(thiscut == Cut.DC_FIDUCIAL_REG3) {
                 if(!cut_DC_FIDUCIAL_REG3()) return false;

            } else if(thiscut == Cut.DELTA_VZ) {
                 if(!cut_DELTA_VZ()) return false;

            } else {
                return false;
            }
        }
        return true;
    }

}
