package uconn.utils.pid.brandon;

import org.jlab.jnp.hipo4.data.Bank;
import org.jlab.clas.physics.LorentzVector;
import org.jlab.detector.base.DetectorType;

// import java.util.HashMap;
import java.util.stream.IntStream;
import uconn.utils.pid.Candidate;

public class ElectronCandidate extends Candidate {
    /// This is the enum for cut strength
    public enum Level {
        LOOSE, ///< loose strength
        STANDARD, ///< standard strength
        STRICT, ///< strict strength
    }

    /// This is the enum for electron cut types
    public enum Cut {
        STATUS,
        CHARGE,
        EBPID,
        // MIN_MOMENTUM,
        VERTEX,
        PCAL_FIDUCIAL,
        ANTI_PION,
        EIEO,
        SAMPLING,
        DC_FIDUCIAL
        // ELE_PID, ///< cut on PDG code
        // CC_NPHE, ///< nphe cut
        // EC_OUTER_VS_INNER, ///< outer vs inner energy deposit
        // EC_SAMPLING, ///< sampling fraction
        // EC_FIDUCIAL, ///< fiducial EC cut
        // DC_FIDUCIAL_REG1, ///< fiducial DC cut for region 1
        // DC_FIDUCIAL_REG2, ///< fiducial DC cut for region 2
        // DC_FIDUCIAL_REG3, ///< fiducial DC cut for region 3
        // DC_VERTEX ///< cut on DC Z vertex
    }

    public boolean ecin_status = false;

    /**
    * return ElectronCandidate instance
    * @param applycuts the list of cuts required to apply
    */
    public static ElectronCandidate getElectronCandidate(int ipart, Bank recbank, Bank calbank, Bank ccbank, Bank trajbank) {
        ElectronCandidate candidate = new ElectronCandidate();
        if(recbank!=null) {
            candidate.setPID(recbank.getInt("pid",ipart));
            candidate.setVZ(recbank.getFloat("vz",ipart));
            candidate.setPxyz(recbank.getFloat("px",ipart), recbank.getFloat("py",ipart), recbank.getFloat("pz",ipart));
            candidate.setStatus(recbank.getShort("status", ipart));
            candidate.setCharge(recbank.getByte("charge", ipart));
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
            } else if(calbank.getByte("layer",i) == 4) {
                candidate.setECINenergy(calbank.getFloat("energy",i));
                candidate.ecin_status = true;
            }
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

    // public void setElectronCutStrictness(HashMap<String,Integer> el_cut_strictness) {
    //   cuts.setElectronCutStrictness(el_cut_strictness);
    // }

    // public void setElectronCutParameters(String magnetic_field_config) {
    //     cuts.setElectronCutParameters(magnetic_field_config);
    //   }

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
            
            if (thiscut == Cut.STATUS) {
                if(status==null) return false;
                else if(!ElectronCuts.STATUS(status)) return false;             
            } else if(thiscut == Cut.CHARGE) {
                if(charge==null) return false; 
                else if(!ElectronCuts.CHARGE(charge)) return false; 
            } else if(thiscut == Cut.EBPID) {
                if(pid==null) return false; 
                else if(!ElectronCuts.EBPID(pid)) return false; 
            } else if(thiscut == Cut.VERTEX) {
                if(vz==null) return false; 
                else if(!ElectronCuts.VERTEX(vz, field==MagField.INBENDING)) return false; 
            } else if(thiscut == Cut.PCAL_FIDUCIAL) {
                if(pcal_sector==null || pcal_lv==null || pcal_lw==null) return false; 
                else if(!ElectronCuts.PCAL_FIDUCIAL(pcal_sector, pcal_lv, pcal_lw, field==MagField.INBENDING)) return false; 
            } else if(thiscut == Cut.ANTI_PION) {
                if (p==null) return false;
                else if (ecin_status==false || pcal_energy==null) { if (p >= 4.5) return false; }
                else if(!ElectronCuts.ANTI_PION(p, pcal_energy, ecin_energy)) return false; 
            } else if(thiscut == Cut.EIEO) {
                if(pcal_energy==null) return false; 
                else if(!ElectronCuts.EIEO(pcal_energy)) return false; 
            } else if(thiscut == Cut.SAMPLING) {
                if( pcal_energy==null || pcal_sector==null || p==null ) return false;
                else if(!ElectronCuts.SAMPLING(ecin_energy, ecout_energy, pcal_energy, pcal_sector, p)) return false;
            } else if(thiscut == Cut.DC_FIDUCIAL) {
                if(dc_sector==null || pid!=11) return false;
                if( traj_x1!=null && traj_y1!=null && traj_z1!=null) {
                    if(field==MagField.INBENDING ? !ElectronCuts.DC_FIDUCIAL_XY(dc_sector, 1, traj_x1, traj_y1) : !ElectronCuts.DC_FIDUCIAL_ThetaPhi(dc_sector, 1, traj_x1, traj_y1, traj_z1)) return false;
                }
                if(traj_x2!=null && traj_y2!=null && traj_z2!=null) {
                    if(field==MagField.INBENDING ? !ElectronCuts.DC_FIDUCIAL_XY(dc_sector, 2, traj_x2, traj_y2) : !ElectronCuts.DC_FIDUCIAL_ThetaPhi(dc_sector, 2, traj_x2, traj_y2, traj_z2)) return false;
                }
                if(traj_x3!=null && traj_y3!=null && traj_z3!=null) {
                    if(field==MagField.INBENDING ? !ElectronCuts.DC_FIDUCIAL_XY(dc_sector, 3, traj_x3, traj_y3) : !ElectronCuts.DC_FIDUCIAL_ThetaPhi(dc_sector, 3, traj_x3, traj_y3, traj_z3)) return false;
                } 
            } else {
                return false;
            }
        }
        return true;
    }

}
