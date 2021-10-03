package uconn.utils.pid.stefan;

import org.jlab.jnp.hipo4.data.Bank;
import org.jlab.clas.physics.LorentzVector;
import org.jlab.detector.base.DetectorType;
import java.util.stream.IntStream;
import uconn.utils.pid.Candidate;

public class ElectronCandidate extends Candidate {
    /// This is the enum for electron cut types
    public enum Cut {
        ELE_PID, ///< cut on PDG code
        CC_NPHE, ///< nphe cut
        EC_OUTER_VS_INNER, ///< outer vs inner energy deposit
        EC_SAMPLING, ///< sampling fraction
        EC_FIDUCIAL, ///< fiducial EC cut
        DC_FIDUCIAL_REG1, ///< fiducial DC cut for region 1
        DC_FIDUCIAL_REG2, ///< fiducial DC cut for region 2
        DC_FIDUCIAL_REG3, ///< fiducial DC cut for region 3
        DC_VERTEX ///< cut on DC Z vertex
    }



    /**
    * \example goodelectron.groovy
    * groovy script to use ElectronCandidate class for finding good electron
    */

     /**
    * return ElectronCandidate instance
    * @param applycuts the list of cuts required to apply
    */
    public static ElectronCandidate getElectronCandidate(int ipart, Bank recbank, Bank calbank, Bank ccbank, Bank trajbank, boolean isinbending) {
        ElectronCandidate candidate = new ElectronCandidate();
        if(!isinbending) candidate.setOUTBENDING();

        if(recbank!=null) {
            candidate.setPID(recbank.getInt("pid",ipart));
            candidate.setVZ(recbank.getFloat("vz",ipart));
            candidate.setPxyz(recbank.getFloat("px",ipart), recbank.getFloat("py",ipart), recbank.getFloat("pz",ipart));
        }
        if(ccbank!=null) IntStream.range(0,ccbank.getRows()).filter(i -> ccbank.getShort("pindex",i) == ipart && ccbank.getByte("detector",i) == DetectorType.HTCC.getDetectorId())
            .findFirst().ifPresent(i -> candidate.setNPHE(ccbank.getFloat("nphe", i)));

        if(calbank!=null) IntStream.range(0,calbank.getRows())
            .filter(i -> calbank.getShort("pindex",i) == ipart && calbank.getByte("detector",i) == DetectorType.ECAL.getDetectorId())
            .forEach(i -> {
            if(calbank.getByte("layer",i) == 1) {
                candidate.setPCALsector(calbank.getByte("sector",i));
                candidate.setPCALenergy(calbank.getFloat("energy",i));
                candidate.setPCALvw(calbank.getFloat("lv",i), calbank.getFloat("lw",i));
            } else if(calbank.getByte("layer",i) == 4)
                candidate.setECINenergy(calbank.getFloat("energy",i));
            else if(calbank.getByte("layer",i) == 7)
                candidate.setECOUTenergy(calbank.getFloat("energy",i));
        });

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
            vec.setPxPyPzM(px,py,pz,0.000511);
        }
        return vec;
    }


    /**
    * testing against all electron cuts
    */
    public boolean iselectron() {
        return iselectron(Cut.values());
    }



    /**
    * assembly of multiple electron cuts
    * @param applycuts the list of cuts required to apply
    */
    public boolean iselectron(Cut ...applycuts) {
        for(Cut thiscut: applycuts) {
            if(thiscut == Cut.ELE_PID) {
                if(pid==null) return false;
                else if(pid!=11) return false;

            } else if(thiscut == Cut.CC_NPHE) {
                if(nphe==null) return false;
                else if(!ElectronCuts.CC_nphe_cut(nphe)) return false;

            } else if(thiscut == Cut.EC_OUTER_VS_INNER) {
                if(pcal_energy==null) return false;
                else if(!ElectronCuts.EC_outer_vs_EC_inner_cut(pcal_energy)) return false;

            } else if(thiscut == Cut.EC_SAMPLING) {
                if(p==null || pcal_sector==null || pcal_energy==null || ecin_energy==null || ecout_energy==null) return false;
                else if(!ElectronCuts.EC_sampling_fraction_cut(p, pcal_sector, pcal_energy, ecin_energy, ecout_energy)) return false;

            } else if(thiscut == Cut.EC_FIDUCIAL) {
                if(pcal_sector==null || pcal_lv==null || pcal_lw==null) return false;
                else if(!ElectronCuts.EC_hit_position_fiducial_cut_homogeneous(pcal_sector, pcal_lv, pcal_lw)) return false;

            } else if(thiscut == Cut.DC_FIDUCIAL_REG1) {
                if(dc_sector==null || traj_x1==null || traj_y1==null || pid==null) return false;
                else if(!ElectronCuts.DC_fiducial_cut_XY(dc_sector, 1, traj_x1, traj_y1, pid, field==MagField.INBENDING)) return false;

            } else if(thiscut == Cut.DC_FIDUCIAL_REG2) {
                if(dc_sector==null || traj_x2==null || traj_y2==null || pid==null) return false;
                else if(!ElectronCuts.DC_fiducial_cut_XY(dc_sector, 2, traj_x2, traj_y2, pid, field==MagField.INBENDING)) return false;

            } else if(thiscut == Cut.DC_FIDUCIAL_REG3) {
                if(dc_sector==null || traj_x3==null || traj_y3==null || pid==null) return false;
                else if(!ElectronCuts.DC_fiducial_cut_XY(dc_sector, 3, traj_x3, traj_y3, pid, field==MagField.INBENDING)) return false;

            } else if(thiscut == Cut.DC_VERTEX) {
                if(pcal_sector==null || vz==null) return false;
                else if(!ElectronCuts.DC_z_vertex_cut(pcal_sector, vz, field==MagField.INBENDING)) return false;

            } else {
                return false;
            }
        }
        return true;
    }

}
