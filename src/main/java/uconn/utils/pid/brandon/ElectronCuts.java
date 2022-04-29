package uconn.utils.pid.brandon;

public class ElectronCuts {

    /**
     * Electron status cut
     * @param status status of electron
     */
    public static boolean STATUS(short status) {
        return status < 0;
    }

    /**
     * Electron charge cut
     * @param charge charge of electron
     */
    public static boolean CHARGE(byte charge) {
        return charge < 0;
    }

    /**
     * Electron PID cut
     * @param pid PID of electron
     */
    public static boolean EBPID(int pid) {
        int ebPID = 11;
        return pid == ebPID;
    }

    /**
     * Electron vertex cut
     * @param vz z vertex
     * @param isinbending True if magnetic field is inbending
     */
    public static boolean VERTEX(double vz, boolean isinbending) { 
        int vz_cut_lvl = 1; //null;

        double vz_min_sect_inb = -13.;
        double vz_max_sect_inb = 12.; 
        
        // not sector dependent as of 08/2020
        double vz_min_sect_outb = -18.;
        double vz_max_sect_outb = 10.; 

        if( vz_cut_lvl == 0 ) { 
            vz_min_sect_inb = vz_min_sect_inb-1.6;
            vz_max_sect_inb = vz_max_sect_inb+2.5;
    
            vz_min_sect_outb = vz_min_sect_outb-1;
            vz_max_sect_outb = vz_max_sect_outb+1;
        }
        else if( vz_cut_lvl == 1 ) { 
            vz_min_sect_inb = vz_min_sect_inb-0;
            vz_max_sect_inb = vz_max_sect_inb+0;
    
            vz_min_sect_outb = vz_min_sect_outb-0;
            vz_max_sect_outb = vz_max_sect_outb+0;
        }
        else if( vz_cut_lvl == 2 ) { 
            vz_min_sect_inb = vz_min_sect_inb+1;
            vz_max_sect_inb = vz_max_sect_inb-1.9;
    
            vz_min_sect_outb = vz_min_sect_outb+1;
            vz_max_sect_outb = vz_max_sect_outb-1;
        }

        double min_vz=vz_min_sect_inb;
        double max_vz=vz_max_sect_inb;

        if (!isinbending) {
            min_vz=vz_min_sect_outb;
            max_vz=vz_max_sect_outb;
        }        

        return (vz < max_vz) && (vz > min_vz);
    }

    /**
     * Electron PCAL fiducial cut
     * @param pcal_sector sector of hits in PCAL
     * @param pcal_lv pcal_lv
     * @param pcal_lw pcal_lw
     * @param isinbending True if magnetic field is inbending
     */
    public static boolean PCAL_FIDUCIAL(int pcal_sector, double pcal_lv, double pcal_lw, boolean isinbending) {
        pcal_sector = pcal_sector-1;

        // double[] s_min_u = null;
        // double[] s_max_u = null;
        double[] s_min_v = null; // only v & w used
        // double[] s_max_v = null;
        double[] s_min_w = null;
        // double[] s_max_w = null;

        // int min_u_cut_lvl = 1;
        int min_v_cut_lvl = 0; // only v used
        int min_w_cut_lvl = 0; // only v used
        // int max_u_cut_lvl = 1;
        // int max_v_cut_lvl = 0;
        // int max_w_cut_lvl = 0;

        // double[] min_u_tight_inb = {4.5,  4.5,  4.5,  4.5,  4.5,  4.5 };
        // double[] min_u_med_inb   = {9.0,  9.0,  9.0,  9.0,  9.0,  9.0 };
        // double[] min_u_loose_inb = {13.5, 13.5, 13.5, 13.5, 13.5, 13.5};
        
        // u shows a slight fall of the sampling fraction for high values
        // double[] max_u_tight_inb = {398., 398., 398., 398., 398., 398.};
        // double[] max_u_med_inb   = {408., 408., 408., 408., 408., 408.};
        // double[] max_u_loose_inb = {420., 420., 420., 420., 420., 420.}; 
        
        // double[][] min_u_cuts_inb = {min_u_loose_inb, min_u_med_inb, min_u_tight_inb};
        // double[][] max_u_cuts_inb = {max_u_loose_inb, max_u_med_inb, max_u_tight_inb};

        // tight: only background outside 3 sigma, medium: 10 % outside 3 sigma, loose: 50% outside 3 sigma 
        double[] min_v_tight_inb = {19.,19.,19.,19.,19.,19.};//{4.5,4.5,4.5,4.5,4.5,4.5}
        double[] min_v_med_inb   = {14.,14.,14.,14.,14.,14.};//{9.0,9.0,9.0,9.0,9.0,9.0}
        double[] min_v_loose_inb = {9.,9.,9.,9.,9.,9.};      //13.5,13.5,13.5,13.5,13.5,13.5}
        
        // the maximum of v is never reached
        // double[] max_v_tight_inb = {400., 400., 400., 400., 400., 400.};
        // double[] max_v_med_inb   = {400., 400., 400., 400., 400., 400.};
        // double[] max_v_loose_inb = {400., 400., 400., 400., 400., 400.};

        double[][] min_v_cuts_inb = {min_v_loose_inb, min_v_med_inb, min_v_tight_inb};
        // double[][] max_v_cuts_inb = {max_v_loose_inb, max_v_med_inb, max_v_tight_inb};
        
        // tight: only background outside 3 sigma, medium: 10 % outside 3 sigma, loose: 50% outside 3 sigma 
        // flip the loose and medium because of weird combos in rga note
        double[] min_w_tight_inb = {19.,19.,19.,19.,19.,19.};//{4.5, 4.5, 4.5, 4.5, 4.5, 4.5}
        double[] min_w_med_inb   = {14.,14.,14.,14.,14.,14.};//{9.0, 9.0, 9.0, 9.0, 9.0, 9.0}
        double[] min_w_loose_inb = {9.,9.,9.,9.,9.,9.};     //{13.5,13.5,13.5,13.5,13.5,13.5}
        
        // the maximum of w is never reached
        // double[] max_w_tight_inb = {400., 400., 400., 400., 400., 400.};
        // double[] max_w_med_inb   = {400., 400., 400., 400., 400., 400.};
        // double[] max_w_loose_inb = {400., 400., 400., 400., 400., 400.};

        double[][] min_w_cuts_inb = {min_w_loose_inb, min_w_med_inb, min_w_tight_inb};
        // double[][] max_w_cuts_inb = {max_w_loose_inb, max_w_med_inb, max_w_tight_inb};
    
        /// outbending (not adjusted up to now, same as inbending!):
        
        // tight: only background outside 3 sigma, medium: 10 % outside 3 sigma, loose: 50% outside 3 sigma 
        // double[] min_u_tight_out = {42., 32., 38., 27.5, 32., 29.};
        // double[] min_u_med_out   = {33., 26., 34., 22., 27., 25.};
        // double[] min_u_loose_out = {28., 22., 30., 18., 22., 22.};
        
        // u shows a slight fall of the sampling fraction for high values
        // double[] max_u_tight_out = {398., 398., 398., 398., 398., 398.};
        // double[] max_u_med_out   = {408., 408., 408., 408., 408., 408.}; 
        // double[] max_u_loose_out = {420., 420., 420., 420., 420., 420.}; 

        // double[][] min_u_cuts_outb = {min_u_loose_out, min_u_med_out, min_u_tight_out};
        // double[][] max_u_cuts_outb = {max_u_loose_out, max_u_med_out, max_u_tight_out};
        
        // tight: only background outside 3 sigma, medium: 10 % outside 3 sigma, loose: 50% outside 3 sigma 
        double[] min_v_tight_out = {18.0, 12.0, 19.5,  15.5,  20.0, 13.0};
        double[] min_v_med_out   = {16.0, 10.5, 17.0,  14.25, 18.0, 11.0};
        double[] min_v_loose_out = {10.25, 8.0, 12.75, 12.5,  13.25, 9.0};
        
        // the maximum of v is never reached
        // double[] max_v_tight_out = {400., 400., 400., 400., 400., 400.};
        // double[] max_v_med_out   = {400., 400., 400., 400., 400., 400.};
        // double[] max_v_loose_out = {400., 400., 400., 400., 400., 400.};

        double[][] min_v_cuts_outb = {min_v_loose_out, min_v_med_out, min_v_tight_out};
        // double[][] max_v_cuts_outb = {max_v_loose_out, max_v_med_out, max_v_tight_out};
        
        // tight: only background outside 3 sigma, medium: 10 % outside 3 sigma, loose: 50% outside 3 sigma 
        double[] min_w_tight_out = {14.0, 18.7, 18.7,  12.0, 16.0, 13.0};
        double[] min_w_med_out   = {11.0, 17.5, 16.25, 7.5,  14.5, 9.25};
        double[] min_w_loose_out = {7.25, 11.0, 13.0,  5.5,  10.0, 6.0};
        
        // the maximum of w is never reached
        // double[] max_w_tight_out = {400., 400., 400., 400., 400., 400.};
        // double[] max_w_med_out   = {400., 400., 400., 400., 400., 400.};
        // double[] max_w_loose_out = {400., 400., 400., 400., 400., 400.};

        double[][] min_w_cuts_outb = {min_w_loose_out, min_w_med_out, min_w_tight_out};
        // double[][] max_w_cuts_outb = {max_w_loose_out, max_w_med_out, max_w_tight_out};

        // s_min_u=min_u_cuts_inb[min_u_cut_lvl]; //min_u_med_out
        s_min_v=min_v_cuts_inb[min_v_cut_lvl];
        s_min_w=min_w_cuts_inb[min_w_cut_lvl];
        
        // s_max_u=max_u_cuts_inb[max_u_cut_lvl];//max_u_med_out
        // s_max_v=max_v_cuts_inb[max_v_cut_lvl];//max_v_med_out
        // s_max_w=max_w_cuts_inb[max_w_cut_lvl];//max_w_med_out
        if(!isinbending) {
            // s_min_u=min_u_cuts_outb[min_u_cut_lvl]; //min_u_med_out
            s_min_v=min_v_cuts_outb[min_v_cut_lvl];
            s_min_w=min_w_cuts_outb[min_w_cut_lvl];
            
            // s_max_u=max_u_cuts_outb[max_u_cut_lvl];//max_u_med_out
            // s_max_v=max_v_cuts_outb[max_v_cut_lvl];//max_v_med_out
            // s_max_w=max_w_cuts_outb[max_w_cut_lvl];//max_w_med_out
        }

        return pcal_lv > s_min_v[pcal_sector] && pcal_lw > s_min_w[pcal_sector];
    }

    /**
     * Electron anti-pion cut
     * @param p particle momentum
     * @param pcal_energy energy deposited in PCAL
     * @param ecin_energy energy deposited in ECIN
     */
    public static boolean ANTI_PION(double p, double pcal_energy, double ecin_energy) { 
        double anti_pion_threshold=0.2;
        if( p >= 4.5) {
            // System.out.println((-pcal_energy/p + anti_pion_threshold) < (ecin_energy/p));
            if (ecin_energy == 0f)
                return false;
            return (double)(-pcal_energy/p + anti_pion_threshold) < (double)(ecin_energy/p);
        }
        return true;
    }

    /**
     * Electron EIEO cut
     * @param pcal_energy energy deposited in PCAL
     */
    public static boolean EIEO(double pcal_energy) { 
        int ecal_cut_lvl = 1;//null;

        // technically the pcal energy
        double min_ecal_inner_dep = 0.07;
        double ecal_inner_dep_loose = -0.01;
        double ecal_inner_dep_med = 0.0;
        double ecal_inner_dep_tight = 0.02;
        
        if( ecal_cut_lvl == 0 ) min_ecal_inner_dep += ecal_inner_dep_loose;
        else if( ecal_cut_lvl == 1 ) min_ecal_inner_dep += ecal_inner_dep_med;
        else if( ecal_cut_lvl == 2 ) min_ecal_inner_dep += ecal_inner_dep_tight;
        return pcal_energy > min_ecal_inner_dep;
    }

    /**
     * Electron Sampling cut
     * @param ecin_energy energy deposited in ECIN
     * @param ecout_energy energy deposited in ECOUT
     * @param pcal_energy energy deposited in PCAL
     * @param pcal_sector sector of hits in PCAL
     * @param p particle momentum
     */
    public static boolean SAMPLING(double ecin_energy, double ecout_energy, double pcal_energy, int pcal_sector, double p) { 

        double sigma_range = 3.5;// to match rga note 08/2020 before it was 4.0
        // updated sampling fraction cuts
        double[][] ecal_e_sampl_mu = {
            {  0.2531 ,  0.2550 ,  0.2514 ,  0.2494 ,  0.2528 ,  0.2521 } , 
            { -0.6502 , -0.7472 , -0.7674 , -0.4913 , -0.3988 , -0.703  } , 
            {  4.939  ,  5.350  ,  5.102  ,  6.440  ,  6.149  ,  4.957  }};
        
        double[][] ecal_e_sampl_sigm = {
            {  2.726e-3 ,  4.157e-3 ,  5.222e-3 ,  5.398e-3 ,  8.453e-3 ,  6.533e-3 },
            {  1.062    ,  0.859    ,  0.5564   ,  0.6576   ,  0.3242   ,  0.4423   }, 
            { -4.089    , -3.318    , -2.078    , -2.565    , -0.8223   , -1.274    }};
            
        double eidep = ecin_energy;
        double eodep = ecout_energy;
        double pcaldep = pcal_energy;
        int sector = pcal_sector-1;
        
        double edep = eidep + eodep + pcaldep;
        if( sector >= 0 ){

            double mean = ecal_e_sampl_mu[0][sector] + ecal_e_sampl_mu[1][sector]/1000*Math.pow(p - ecal_e_sampl_mu[2][sector],2);
            double sigma = ecal_e_sampl_sigm[0][sector] + ecal_e_sampl_sigm[1][sector]/(10*(p - ecal_e_sampl_sigm[2][sector]));

            double upper_cut = mean + sigma_range * sigma;
            double lower_cut = mean - sigma_range * sigma;
            
            if( edep/p <= upper_cut && edep/p >= lower_cut ) return true;
        }
        return false;
    }

    /**
     * Electron DC_FIDUCIAL_XY cut
     * @param dc_sector sector of hits in DC
     * @param region specify fiducial cuts for which region to use
     * @param x x for region 1 or 2 or 3 from REC::Traj
     * @param y y for region 1 or 2 or 3 from REC::Traj
     */
    public static boolean DC_FIDUCIAL_XY(int dc_sector, int region, double x, double y) {

        double[][][] dc_fiducial_cutXY_min = {{{7.62814, -0.56319},{18.2833, -0.587275},{20.2027, -0.54605}},
            {{9.20907, -0.586977},{10.493, -0.544243},{23.0759, -0.581959}},
            {{12.5459, -0.631322},{20.5635, -0.618555},{26.3621, -0.576806}},
            {{8.36343, -0.552394},{14.7596, -0.554798},{29.5554, -0.60545}},
            {{16.3732, -0.663303},{10.0255, -0.533019},{31.6086, -0.617053}},
            {{8.20222, -0.567211},{20.0181, -0.605458},{22.2098, -0.567599}}};

        double[][][] dc_fiducial_cutXY_max = {{{-7.49907,0.583375},{-18.8174,0.599219},{-23.9353,0.574699}},
            {{-14.0547,0.631533},{-14.4223,0.597079},{-14.838,0.547436}},
            {{-7.72508,0.578501},{-18.7928,0.56725},{-29.9003,0.612354}},
            {{-6.12844,0.566777},{-13.6772,0.573262},{-26.1895,0.591816}},
            {{-20.0718,0.670941},{-9.4775,0.511748},{-28.0869,0.590488}},
            {{-9.52924,0.591687},{-17.8564,0.596417},{-23.5661,0.576317}}};

        double X = x;
        double Y = y;

        // System.out.println(region);
        // System.out.println(dc_sector);
        // System.out.println(X);
        // System.out.println(Y);


        double X_new = X * Math.cos(Math.toRadians(-60*(dc_sector-1))) - Y * Math.sin(Math.toRadians(-60*(dc_sector-1)));
        Y = X * Math.sin(Math.toRadians(-60*(dc_sector-1))) + Y * Math.cos(Math.toRadians(-60*(dc_sector-1)));
        X = X_new;
            
        region=region-1; // shift to match indexing for arrays
        double calc_min = dc_fiducial_cutXY_min[dc_sector - 1][region][0] + dc_fiducial_cutXY_min[dc_sector - 1][region][1] * X;
        double calc_max = dc_fiducial_cutXY_max[dc_sector - 1][region][0] + dc_fiducial_cutXY_max[dc_sector - 1][region][1] * X;

        // System.out.print(region);
        // System.out.println((Y > calc_min) && (Y < calc_max));

        // System.out.println(X);
        // System.out.println(Y);
        // System.out.println(calc_min);
        // System.out.println(calc_max);
        

        return ((Y > calc_min) && (Y < calc_max));
    }

    /**
     * Electron DC_FIDUCIAL_ThetaPhi cut
     * @param dc_sector sector of hits in DC
     * @param region specify fiducial cuts for which region to use
     * @param x x for region 1 or 2 or 3 from REC::Traj
     * @param y y for region 1 or 2 or 3 from REC::Traj
     */
    public static boolean DC_FIDUCIAL_ThetaPhi(int dc_sector, int region, double x, double y, double z) { // Need tightness level
        double[][][][] dc_fiducial_cutThetaPhi_min = {
            {{{37.289, -27.5201,1.12866, -0.00526111},{45.3103, -33.5226,1.72923, -0.0114495},{61.5709, -47.6158,3.4295, -0.0316429}},
             {{36.6259, -27.4064,1.16617, -0.00604629},{50.3751, -37.5848,2.19621, -0.0169241},{35.1563, -26.514,1.09795, -0.00545864}},
             {{27.2367, -20.3068,0.517752, -0.000335432},{39.0489, -28.6903,1.24306, -0.0065226},{41.0208, -30.0339,1.30776, -0.00626721}},
             {{29.261, -21.7041,0.613556, -0.000774652},{39.5304, -29.1388,1.34116, -0.00823818},{44.5313, -33.4056,1.77581, -0.0123965}},
             {{36.5659, -25.119,0.714074, -2.65397e-11},{31.6524, -22.6934,0.613977, -5.46634e-10},{34.7312, -24.9901,0.749061, -1.22922e-09}},
             {{33.154, -23.8803,0.685794, -1.13236e-10},{42.6731, -31.0799,1.40425, -0.00730816},{46.4732, -35.6988,2.10144, -0.0164771}}}
            };
        
        double[][][][] dc_fiducial_cutThetaPhi_max = {
            {{{-35.1716, 25.102, -0.750281, 5.34679e-05},{-39.1633, 28.5551, -1.13429, 0.00419047},{-33.7705, 24.8068, -0.811239, 0.00138345}},
             {{-36.2389, 26.7979, -1.08147, 0.0050898},{-43.643, 31.6783, -1.49203, 0.00872922},{-54.4042, 40.6516, -2.52393, 0.0205649}},
             {{-38.3238, 26.1667, -0.777077, 0.000264835},{-34.2011, 24.2843, -0.696392, 3.75866e-12},{-36.4636, 25.8712, -0.786592, 2.24421e-10}},
             {{-31.8019, 23.154, -0.653992, 2.69968e-05},{-34.6637, 24.6043, -0.714901, 2.02675e-10},{-36.7209, 26.2469, -0.828638, 0.000340435}},
             {{-33.4016, 24.6901, -0.779889, 0.000430557},{-35.4583, 24.7491, -0.707953, 2.18559e-10},{-37.7335, 28.1547, -1.1986, 0.00582395}},
             {{-34.7808, 24.6988, -0.719936, 5.73299e-10},{-54.5797, 40.9138, -2.57493, 0.0213354},{-38.4972, 28.3142, -1.21741, 0.00640373}}}
            };

        double theta_DCr = (180/Math.PI) * Math.acos(z / Math.sqrt( Math.pow(x,2) + Math.pow(y,2) + Math.pow(z,2) ) );
        double phi_DCr_raw = (180/Math.PI) * Math.atan2( y / Math.sqrt( Math.pow(x,2) + Math.pow(y,2) + Math.pow(z,2) ) , x/Math.sqrt( Math.pow(x,2) + Math.pow(y,2) + Math.pow(z,2)));
    
        // rotate coordinates
        double phi_DCr = 5000;
        
        if (dc_sector == 1) phi_DCr = phi_DCr_raw;
        if (dc_sector == 2) phi_DCr = phi_DCr_raw - 60;
        if (dc_sector == 3) phi_DCr = phi_DCr_raw - 120;
        if (dc_sector == 4 && phi_DCr_raw > 0) phi_DCr = phi_DCr_raw - 180;
        if (dc_sector == 4 && phi_DCr_raw < 0) phi_DCr = phi_DCr_raw + 180;
        if (dc_sector == 5) phi_DCr = phi_DCr_raw + 120;
        if (dc_sector == 6) phi_DCr = phi_DCr_raw + 60;
        
        // shift to match array syntax
        region = region - 1;
        
        // Typo in groovy code makes only first half of these statements compile
        double calc_phi_min = dc_fiducial_cutThetaPhi_min[0][dc_sector - 1][region][0] + dc_fiducial_cutThetaPhi_min[0][dc_sector - 1][region][1] * Math.log(theta_DCr)
            + dc_fiducial_cutThetaPhi_min[0][dc_sector - 1][region][2] * theta_DCr + dc_fiducial_cutThetaPhi_min[0][dc_sector - 1][region][3] * theta_DCr * theta_DCr;
        
        double calc_phi_max = dc_fiducial_cutThetaPhi_max[0][dc_sector - 1][region][0] + dc_fiducial_cutThetaPhi_max[0][dc_sector - 1][region][1] * Math.log(theta_DCr)
            + dc_fiducial_cutThetaPhi_max[0][dc_sector - 1][region][2] * theta_DCr + dc_fiducial_cutThetaPhi_max[0][dc_sector - 1][region][3] * theta_DCr * theta_DCr;
        
        return ((phi_DCr > calc_phi_min) && (phi_DCr < calc_phi_max));
    }

}
