package biz.zenpets.trainers.utils.models.trainers.modules;

import android.graphics.Bitmap;

public class ModuleAlbumData {

    private Bitmap bmpModuleImage;
    private String txtImageNumber;

    public Bitmap getBmpModuleImage() {
        return bmpModuleImage;
    }

    public void setBmpModuleImage(Bitmap bmpModuleImage) {
        this.bmpModuleImage = bmpModuleImage;
    }

    public String getTxtImageNumber() {
        return txtImageNumber;
    }

    public void setTxtImageNumber(String txtImageNumber) {
        this.txtImageNumber = txtImageNumber;
    }
}