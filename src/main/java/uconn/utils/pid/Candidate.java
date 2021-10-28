package uconn.utils.pid;

public class Candidate {
    /*! \mainpage java classes with PID functions
     * \section Introduction
     * It's a collection of Java classes to be used for PID and event selections.
     * Go to Classes and Examples.
     */

    /// This is the enum for magnetic field configuation
    protected enum MagField {
        INBENDING, ///< inbending
        OUTBENDING ///< outbending
    }


    /// This is the enum for some cut levels
    public enum Level {
        LOOSEST, ///< loosest
        LOOSE, ///< loose
        MEDIUM, ///< medium
        TIGHT ///< tight
    }


    /** A cosntructor
     * @param ipart particle index
     */
    public Candidate(int ipart) {
        this.ipart = ipart;
    }


    public final int ipart;

    protected MagField field = MagField.INBENDING; ///< magnetic field, INBENDING by default
    protected Float nphe = null; ///< number of photoelectrons
    protected Integer pcal_sector = null; ///< pcal sector
    protected Float pcal_energy = null; ///< energy deposited in PCAL
    protected Float ecin_energy = 0f; ///< energy deposited in ECin
    protected Float ecout_energy = 0f; ///< energy deposited in ECout
    protected Float pcal_lv = null; ///< distance on V-side
    protected Float pcal_lw = null; ///< distance on W-side
    protected Float p = null; ///< momentum
    protected Float px = null; ///< px momentum
    protected Float py = null; ///< px momentum
    protected Float pz = null; ///< px momentum
    protected Float vz = null; ///< z vertex
    protected Float beta = null; ///< beta value
    protected Float chi2pid = null; ///< chi2pid value
    protected Float dvz = null; ///< the difference between trigger particle vz and candidate vz
    protected Float traj_x1 = null; ///< x-position of the track at the detector surface (cm) at region 1
    protected Float traj_y1 = null; ///< y-position of the track at the detector surface (cm) at region 1
    protected Float traj_z1 = null; ///< z-position of the track at the detector surface (cm) at region 1
    protected Float traj_x2 = null; ///< x-position of the track at the detector surface (cm) at region 2
    protected Float traj_y2 = null; ///< y-position of the track at the detector surface (cm) at region 2
    protected Float traj_z2 = null; ///< z-position of the track at the detector surface (cm) at region 2
    protected Float traj_x3 = null; ///< x-position of the track at the detector surface (cm) at region 3
    protected Float traj_y3 = null; ///< y-position of the track at the detector surface (cm) at region 3
    protected Float traj_z3 = null; ///< z-position of the track at the detector surface (cm) at region 3
    protected Integer pid = null; ///< particle PID
    protected Integer dc_sector = null; ///< dc sector
    protected Short status = null; ///< particle status


    /**
     * set inbending magnetic field
     */
    public void setINBENDING() {
        this.field = MagField.INBENDING;
    }


    /**
     * set outbending magnetic field
     */
    public void setOUTBENDING() {
        this.field = MagField.OUTBENDING;
    }


    /**
     * @param px set px momentum
     * @param py set py momentum
     * @param pz set pz momentum
     */
    public void setPxyz(Number px, Number py, Number pz) {
        this.px = px==null ? null : px.floatValue();
        this.py = py==null ? null : py.floatValue();
        this.pz = pz==null ? null : pz.floatValue();
        if(this.px!=null && this.py!=null && this.pz!=null)
            this.p = (float) Math.sqrt(this.px*this.px + this.py*this.py + this.pz*this.pz);
    }


    /**
     * @param vz set vertex z position
     */
    public void setVZ(Number vz) {
        this.vz = vz==null ? null : vz.floatValue();
    }


    /**
     * @param beta set beta value for particle
     */
    public void setBETA(Number beta) {
        this.beta = beta==null ? null : beta.floatValue();
    }


    /**
     * @param chi2pid set chi2pid value
     */
    public void setCHI2PID(Number chi2pid) {
        this.chi2pid = chi2pid==null ? null : chi2pid.floatValue();
    }


    /**
     * @param dvz set the difference between vertex z position of candidate and trigger particle
     */
    public void setDVZ(Number dvz) {
        this.dvz = dvz==null ? null : dvz.floatValue();
    }


    /**
     * @param pid set particle PID
     */
    public void setPID(Number pid) {
        this.pid = pid==null ? null : pid.intValue();
    }


    /**
     * @param status set particle status
     */
    public void setStatus(Number status) {
        this.status = status==null ? null : status.shortValue();
    }


    /**
     * @param nphe set number of photoelectrons
     */
    public void setNPHE(Number nphe) {
        this.nphe = nphe==null ? null : nphe.floatValue();
    }


    /**
     * @param sector set PCAL sector
     */
    public void setPCALsector(Number sector) {
        this.pcal_sector = sector==null ? null : sector.intValue();
    }


    /**
     * @param energy set PCAL energy
     */
    public void setPCALenergy(Number energy) {
        this.pcal_energy = energy==null ? null : energy.floatValue();
    }


    /**
     * @param energy set ECIN energy
     */
    public void setECINenergy(Number energy) {
        this.ecin_energy = energy==null ? null : energy.floatValue();
    }


    /**
     * @param energy set ECOUT energy
     */
    public void setECOUTenergy(Number energy) {
        this.ecout_energy = energy==null ? null : energy.floatValue();
    }


    /**
     * @param lv set PCAL lv
     * @param lw set PCAL lw
     */
    public void setPCALvw(Number lv, Number lw) {
        this.pcal_lv = lv==null ? null : lv.floatValue();
        this.pcal_lw = lw==null ? null : lw.floatValue();
    }


    /**
     * @return pcal sector
     */
    public Integer getPCALsector() {
        return pcal_sector;
    }


    /**
     * @return dc sector
     */
    public Integer getDCsector() {
        return dc_sector;
    }


    /**
     * @return DC1 x
     */
    public Float getDC1x() {
        return traj_x1;
    }

 
    /**
     * @return DC1 y
     */
    public Float getDC1y() {
        return traj_y1;
    }

 
    /**
     * @return DC1 z
     */
    public Float getDC1z() {
        return traj_z1;
    }

 
    /**
     * determine DC sector
     * @param x x-coordinate in DC region
     * @param y y-coordinate in DC region
     * @param z z-coordinate in DC region
     */
    private Integer getDCsector(Float x, Float y, Float z) {
        double rr = Math.sqrt(x*x+y*x+z*z);
        double phi = Math.toDegrees(Math.atan2(y/rr, x/rr));

        if(phi < 30 && phi >= -30) return 1;
        else if(phi < 90 && phi >= 30) return 2;
        else if(phi < 150 && phi >= 90) return 3;
        else if(phi >= 150 || phi < -150) return 4;
        else if(phi < -90 && phi >= -150) return 5;
        else if(phi < -30 && phi >= -90) return 6;

        return null;
    }

    /**
     * @param region specify DC region
     * @param x x-coordinate in DC region
     * @param y y-coordinate in DC region
     * @param z z-coordinate in DC region
     */
    public void setDCxyz(int region, Number x, Number y, Number z) {
        if(region==1) {
            this.traj_x1 = x==null ? null : x.floatValue();
            this.traj_y1 = y==null ? null : y.floatValue();
            this.traj_z1 = z==null ? null : z.floatValue();
        } else if(region==2) {
            this.traj_x2 = x==null ? null : x.floatValue();
            this.traj_y2 = y==null ? null : y.floatValue();
            this.traj_z2 = z==null ? null : z.floatValue();
            if(traj_x2!=null && traj_y2!=null && traj_z2!=null) this.dc_sector = getDCsector(traj_x2, traj_y2, traj_z2);
        } else if(region==3) {
            this.traj_x3 = x==null ? null : x.floatValue();
            this.traj_y3 = y==null ? null : y.floatValue();
            this.traj_z3 = z==null ? null : z.floatValue();
        }
    }


}
