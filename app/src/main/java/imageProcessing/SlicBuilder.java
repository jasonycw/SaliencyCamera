package imageProcessing;

/**
 * Created by Jason on 12/10/2014.
 */
public class SlicBuilder {
    private int _nx = 15;
    private int _ny = 15;
    private int _m = 20;

    public SlicBuilder(){
    }

    public SLIC buildSLIC(){
        return new SLIC(_nx,_ny,_m);
    }

    public SlicBuilder nx(int _nx){
        this._nx = _nx;
        return this;
    }

    public SlicBuilder ny(int _ny){
        this._ny  = _ny ;
        return this;
    }

    public SlicBuilder m(int _m ){
        this._m = _m;
        return this;
    }
}
