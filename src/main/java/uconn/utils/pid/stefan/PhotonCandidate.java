package uconn.utils.pid.stefan;

import org.jlab.jnp.hipo4.data.Bank;
import org.jlab.clas.physics.LorentzVector;
import org.jlab.detector.base.DetectorType;
import java.util.stream.IntStream;
import uconn.utils.pid.Candidate;

public class PhotonCandidate extends Candidate {
    /// This is the enum for photon cut types
    public enum Cut {
        PHOTON_PID, ///< cut on PDG code
        EC_FIDUCIAL, ///< fiducial EC cut
        BETA ///< cut on beta
    }



    /**
    * groovy script to use PhotonCandidate class for finding good photon
    */

     /**
    * return PhotonCandidate instance
    * @param applycuts the list of cuts required to apply
    */
    public static PhotonCandidate getPhotonCandidate(int ipart, Bank recbank, Bank calbank, boolean isinbending) {
        PhotonCandidate candidate = new PhotonCandidate();
        if(!isinbending) candidate.setOUTBENDING();

        if(recbank!=null) {
            candidate.setPID(recbank.getInt("pid",ipart));
            candidate.setBETA(recbank.getFloat("beta",ipart));
            candidate.setPxyz(recbank.getFloat("px",ipart), recbank.getFloat("py",ipart), recbank.getFloat("pz",ipart));
        }

        if(calbank!=null) IntStream.range(0,calbank.getRows())
            .filter(i -> calbank.getShort("pindex",i) == ipart && calbank.getByte("detector",i) == DetectorType.ECAL.getDetectorId())
            .forEach(i -> {
            if(calbank.getByte("layer",i) == 1) {
                candidate.setPCALsector(calbank.getByte("sector",i));
                candidate.setPCALvw(calbank.getFloat("lv",i), calbank.getFloat("lw",i));
            }
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
            vec.setPxPyPzM(px,py,pz,0);
        }
        return vec;
    }


    /**
    * testing against all photon cuts
    */
    public boolean isphoton() {
        return isphoton(Cut.values());
    }



    /**
    * assembly of multiple photon cuts
    * @param applycuts the list of cuts required to apply
    */
    public boolean isphoton(Cut ...applycuts) {
        for(Cut thiscut: applycuts) {
            if(thiscut == Cut.PHOTON_PID) {
                if(pid == null) return false;
                else if(pid != 22) return false;

            } else if(thiscut == Cut.EC_FIDUCIAL) {
                if(pcal_sector == null || pcal_lv == null || pcal_lw == null) return false;
                else if(!ElectronCuts.EC_hit_position_fiducial_cut_homogeneous(pcal_sector, pcal_lv, pcal_lw, ElectronCuts.Level.MEDIUM)) return false;

            } else if(thiscut == Cut.BETA) {
                if(beta == null) return false;
                else if(beta > 1.1 || beta < 0.9) return false;

            } else {
                return false;
            }
        }
        return true;
    }

}
