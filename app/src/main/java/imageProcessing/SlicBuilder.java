package imageProcessing;

import android.content.Context;

/**
 * Created by Jason on 12/10/2014.
 */
public class SlicBuilder {
    private int _nx = 15;
    private int _ny = 15;
    private int _m = 20;
    private Context _context = null;

    public SlicBuilder(){
    }

    public SLIC buildSLIC(){
        if(_context==null)
            return new SLIC(_nx,_ny,_m);
        else{
            SLIC slic = new SLIC(_nx,_ny,_m);
            slic.setContext(_context);
            return slic;
        }
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

    public SlicBuilder context(Context _context){
        this._context = _context;
        return this;
    }
}
