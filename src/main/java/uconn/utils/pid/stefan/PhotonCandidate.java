package uconn.utils.pid.stefan;

import org.jlab.jnp.hipo4.data.Bank;
import org.jlab.clas.physics.LorentzVector;
import org.jlab.detector.base.DetectorType;
import java.util.stream.IntStream;
import java.util.EnumMap;
import uconn.utils.pid.Candidate;


public class PhotonCandidate extends Candidate {
    /// This is the enum for photon cut types
    public enum Cut {
        PID, ///< cut on PDG code
        FORWARD, ///< only forward detector
        EC_FIDUCIAL, ///< fiducial EC cut
        BETA ///< cut on beta
    }



    /**
     * groovy script to use PhotonCandidate class for finding good photon
     */



    /** A Constructor
     * @param ipart particle index
     */
    public PhotonCandidate(int ipart) {
        super(ipart);
    }



    /**
     * return PhotonCandidate instance
     * @param ipart particle index
     * @param recbank,calbank particle and calorimeter banks
     * @param isinbending true for inbending, false for outbending
     */
    public static PhotonCandidate getPhotonCandidate(int ipart, Bank recbank, Bank calbank, boolean isinbending) {
        PhotonCandidate candidate = new PhotonCandidate(ipart);
        if(!isinbending) candidate.setOUTBENDING();

        if(recbank!=null) {
            candidate.setPID(recbank.getInt("pid",ipart));
            candidate.setStatus(recbank.getShort("status",ipart));
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
     * @return pid cut
     */
    public boolean cut_PID() {
        if(pid == null) return false;
        return pid == 22;
    }



    /**
     * @return EC fiducial cut with loose cut
     */
    public boolean cut_EC_FIDUCIAL() {
        return cut_EC_FIDUCIAL(Level.LOOSE);
    }



    /**
     * @return EC fiducial cut
     */
    public boolean cut_EC_FIDUCIAL(Level eclevel) {
        if(pcal_sector == null || pcal_lv == null || pcal_lw == null) return false;
        return ElectronCuts.EC_hit_position_fiducial_cut_homogeneous(pcal_sector, pcal_lv, pcal_lw, eclevel);
    }



    /**
     * @return if it is detected in forward
     */
    public boolean cut_FORWARD() {
        if(status==null) return false;
        return status>=2000 && status<4000;
    }



    /**
     * @return beta cut
     */
    public boolean cut_BETA() {
        if(beta == null) return false;
        return beta > 0.9 && beta < 1.1;
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

            if(thiscut == Cut.PID) {
                if(!cut_PID()) return false;

            } else if(thiscut == Cut.FORWARD) {
                if(!cut_FORWARD()) return false;

            } else if(thiscut == Cut.EC_FIDUCIAL) {
                if(!cut_EC_FIDUCIAL()) return false;

            } else if(thiscut == Cut.BETA) {
                if(!cut_BETA()) return false;

            } else {
                return false;
            }
        }
        return true;
    }

}
