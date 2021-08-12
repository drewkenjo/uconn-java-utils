package uconn.utils.pid.brandon;

import org.jlab.clas.physics.LorentzVector;
import org.jlab.detector.base.DetectorType;
import java.util.stream.IntStream;
import org.jlab.jnp.hipo4.data.Bank;
import uconn.utils.pid.Candidate;

public class ProtonCandidate extends Candidate {
    /// This is the enum for cut strength
    public enum Level {
        LOOSE, ///< loose strength
        STANDARD, ///< standard strength
        STRICT, ///< strict strength
    }

    /// This is the enum for proton cut types
    public enum Cut {
        HadronEBPIDCut,             ///< cut on PDG code
        TrajChi2Cut,                ///< cut on chi2pid
        DCFiducialCutChi2Region1,   ///< fiducial DC cut for region 1
        DCFiducialCutChi2Region2,   ///< fiducial DC cut for region 2
        DCFiducialCutChi2Region3    ///< fiducial DC cut for region 3
    }



    /**
    * return ProtonCandidate instance
    * @param applycuts the list of cuts required to apply
    */
    public static ProtonCandidate getProtonCandidate(int ipart, Bank recbank, Bank trajbank) {
        ProtonCandidate candidate = new ProtonCandidate();
        if(recbank!=null) {
            candidate.setPID(recbank.getInt("pid",ipart));
            candidate.setStatus(recbank.getShort("status",ipart));
            candidate.setVZ(recbank.getFloat("vz",ipart));
            candidate.setDVZ(recbank.getFloat("vz",ipart) - recbank.getFloat("vz",0));
            candidate.setPxyz(recbank.getFloat("px",ipart), recbank.getFloat("py",ipart), recbank.getFloat("pz",ipart));
            candidate.setCHI2PID(recbank.getFloat("chi2pid", ipart));
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
            vec.setPxPyPzM(px,py,pz,0.938272);
        }
        return vec;
    }


    /**
    * testing against all proton cuts
    */
    public boolean isproton() {
        return isproton(Cut.values());
    }



    /**
    * assembly of multiple proton cuts
    * @param applycuts the list of cuts required to apply
    */
    public boolean isproton(Cut ...applycuts) {
        for(Cut thiscut: applycuts) {
            if(thiscut == Cut.HadronEBPIDCut) {
                if(pid==null) return false;
                else if(pid!=2212) return false;
            // } else if(thiscut == Cut.FORWARD) {
            //     if(status==null) return false;
            //     else if(status>=4000) return false;
            //     else if(status<2000) return false;
            } else if(thiscut == Cut.TrajChi2Cut) {
                if(pid==null || chi2pid==null) return false;
                else if(!HadronCuts.TrajChi2Cut(chi2pid)) return false;
            } else if(thiscut == Cut.DCFiducialCutChi2Region1) {
                if(dc_sector==null || traj_x1==null || traj_y1==null || traj_z1==null || pid==null) return false;
                else if(!HadronCuts.DCFiducialCutChi2(dc_sector, 1, traj_x1, traj_y1, traj_z1, pid)) return false;
            } else if(thiscut == Cut.DCFiducialCutChi2Region2) {
                if(dc_sector==null || traj_x2==null || traj_y2==null || traj_z2==null || pid==null) return false;
                else if(!HadronCuts.DCFiducialCutChi2(dc_sector, 2, traj_x2, traj_y2, traj_z2, pid)) return false;
            } else if(thiscut == Cut.DCFiducialCutChi2Region3) {
                if(dc_sector==null || traj_x3==null || traj_y3==null || traj_z3==null || pid==null) return false;
                else if(!HadronCuts.DCFiducialCutChi2(dc_sector, 3, traj_x3, traj_y3, traj_z3, pid)) return false;
            } else {
                return false;
            }
        }
        return true;
    }

}
