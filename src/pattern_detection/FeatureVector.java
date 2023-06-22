package pattern_detection;

import com.github.javaparser.ast.CompilationUnit;

public class FeatureVector {

    /** Constructors */

    public FeatureVector(double civ, double noa, double noc, double nocon, double nois, double nom, double noo, double nam, double cl, double locom1, double locom2, double locom3, double tcc, double ac, double cc, double ec, double mdc, double norm, double rfc, double woc, double wmpc1, double wmpc2, double aofd, double auf, double chc, double cm, double coc, double cbo, double dac, double dd, double fo, double mic, double noed, double vod, double wcm, double doih, double nocc, double trap, double trau, double trdp, double trdu, double mnol, double mnop, double noam, double noom) {
        this.civ = civ;
        this.noa = noa;
        this.noc = noc;
        this.nocon = nocon;
        this.nois = nois;
        this.nom = nom;
        this.noo = noo;
        this.nam = nam;
        this.cl = cl;
        this.locom1 = locom1;
        this.locom2 = locom2;
        this.locom3 = locom3;
        this.tcc = tcc;
        this.ac = ac;
        this.cc = cc;
        this.ec = ec;
        this.mdc = mdc;
        this.norm = norm;
        this.rfc = rfc;
        this.woc = woc;
        this.wmpc1 = wmpc1;
        this.wmpc2 = wmpc2;
        this.aofd = aofd;
        this.auf = auf;
        this.chc = chc;
        this.cm = cm;
        this.coc = coc;
        this.cbo = cbo;
        this.dac = dac;
        this.dd = dd;
        this.fo = fo;
        this.mic = mic;
        this.noed = noed;
        this.vod = vod;
        this.wcm = wcm;
        this.doih = doih;
        this.nocc = nocc;
        this.trap = trap;
        this.trau = trau;
        this.trdp = trdp;
        this.trdu = trdu;
        this.mnol = mnol;
        this.mnop = mnop;
        this.noam = noam;
        this.noom = noom;
    }

    public FeatureVector(CompilationUnit cu) {
        // TODO -> setting OO metrics base on the project
    }

    /** Object-Oriented Metrics */
    // Basic
    private double civ; // Class Interface Width
    private double noa; // Number Of Attributes
    private double noc; // Number Of Classes
    private double nocon; // Number Of Constructors
    private double nois; // Number Of Import Statements
    private double nom; // Number Of Members
    private double noo; // Number Of Operations
    private double nam; // Number of Accessor Methods
    // Cohesion
    private double cl; // Class Locality
    private double locom1; // Lack of Cohesion Of Methods 1
    private double locom2; // Lack of Cohesion Of Methods 2
    private double locom3; // Lack of Cohesion Of Methods 3
    private double tcc; // Tight Class Cohesion
    // Complexity
    private double ac; // Attribute Complexity
    private double cc; // Cyclomatic Complexity
    private double ec; // Essential Complexity
    private double mdc; // Module Design Complexity
    private double norm; // Number Of Remote Methods
    private double rfc; // Response For Class
    private double woc; // Weight Of a Class
    private double wmpc1; // Weighted Methods Per Class 1
    private double wmpc2; // Weighted Methods Per Class 2
    // Coupling
    private double aofd; // Access Of Foreign Data
    private double auf; // Average Use of Interface
    private double chc; // Changing Classes
    private double cm; // Changing Methods
    private double coc; // Clients Of Class
    private double cbo; // Coupling Between Objects
    private double dac; // Data Abstraction Coupling
    private double dd; // Dependency Dispersion
    private double fo; // FanOut
    private double mic; // Method Invocation Coupling
    private double noed; // Number Of External Dependencies
    private double vod; // Violations of Demeter's Law
    private double wcm; // Weighted Changing Methods
    // Inheritance
    private double doih; // Depth Of Inheritance Hierarchy
    private double nocc; // Number Of Child Classes
    // Inheritance-based Coupling
    private double trap; // Total Reuse of Ancestor percentage
    private double trau; // Total Reuse of Ancestor unitary
    private double trdp; // Total Reuse in Descendants percentage
    private double trdu; // Total Reuse in Descendants unitary
    // Maximum Type
    private double mnol; // Maximum Number Of Levels
    private double mnop; // Maximum Number Of Parameters
    // Polymorphism
    private double noam; // Number Of Added Methods
    private double noom; // Number Of Overridden Methods

    /** Extracting Metrics From a Project */
    private void setCiv(double civ) {
        this.civ = civ;
    }

    private void setNoa(double noa) {
        this.noa = noa;
    }

    private void setNoc(double noc) {
        this.noc = noc;
    }

    private void setNocon(double nocon) {
        this.nocon = nocon;
    }

    private void setNois(double nois) {
        this.nois = nois;
    }

    private void setNom(double nom) {
        this.nom = nom;
    }

    private void setNoo(double noo) {
        this.noo = noo;
    }

    private void setNam(double nam) {
        this.nam = nam;
    }

    private void setCl(double cl) {
        this.cl = cl;
    }

    private void setLocom1(double locom1) {
        this.locom1 = locom1;
    }

    private void setLocom2(double locom2) {
        this.locom2 = locom2;
    }

    private void setLocom3(double locom3) {
        this.locom3 = locom3;
    }

    private void setTcc(double tcc) {
        this.tcc = tcc;
    }

    private void setAc(double ac) {
        this.ac = ac;
    }

    private void setCc(double cc) {
        this.cc = cc;
    }

    private void setEc(double ec) {
        this.ec = ec;
    }

    private void setMdc(double mdc) {
        this.mdc = mdc;
    }

    private void setNorm(double norm) {
        this.norm = norm;
    }

    private void setRfc(double rfc) {
        this.rfc = rfc;
    }

    private void setWoc(double woc) {
        this.woc = woc;
    }

    private void setWmpc1(double wmpc1) {
        this.wmpc1 = wmpc1;
    }

    private void setWmpc2(double wmpc2) {
        this.wmpc2 = wmpc2;
    }

    private void setAofd(double aofd) {
        this.aofd = aofd;
    }

    private void setAuf(double auf) {
        this.auf = auf;
    }

    private void setChc(double chc) {
        this.chc = chc;
    }

    private void setCm(double cm) {
        this.cm = cm;
    }

    private void setCoc(double coc) {
        this.coc = coc;
    }

    private void setCbo(double cbo) {
        this.cbo = cbo;
    }

    private void setDac(double dac) {
        this.dac = dac;
    }

    private void setDd(double dd) {
        this.dd = dd;
    }

    private void setFo(double fo) {
        this.fo = fo;
    }

    private void setMic(double mic) {
        this.mic = mic;
    }

    private void setNoed(double noed) {
        this.noed = noed;
    }

    private void setVod(double vod) {
        this.vod = vod;
    }

    private void setWcm(double wcm) {
        this.wcm = wcm;
    }

    private void setDoih(double doih) {
        this.doih = doih;
    }

    private void setNocc(double nocc) {
        this.nocc = nocc;
    }

    private void setTrap(double trap) {
        this.trap = trap;
    }

    private void setTrau(double trau) {
        this.trau = trau;
    }

    private void setTrdp(double trdp) {
        this.trdp = trdp;
    }

    private void setTrdu(double trdu) {
        this.trdu = trdu;
    }

    private void setMnol(double mnol) {
        this.mnol = mnol;
    }

    private void setMnop(double mnop) {
        this.mnop = mnop;
    }

    private void setNoam(double noam) {
        this.noam = noam;
    }

    private void setNoom(double noom) {
        this.noom = noom;
    }
}
