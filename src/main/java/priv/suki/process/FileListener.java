package priv.suki.process;

import priv.suki.util.Propert;
import org.apache.log4j.helpers.FileWatchdog;

/**
 * @author »¨Π‘ηχ
 * @version 1.0.3
 */
public class FileListener {

    public FileListener(String filepath) {

        GloablConfig gloablconfig = new GloablConfig(filepath);
        gloablconfig.setDelay(500);
        gloablconfig.start();
    }


    public static class GloablConfig extends FileWatchdog {
        GloablConfig(String filename) {
            super(filename);
        }

        @Override
        protected void doOnChange() {
            Propert.getPropert().setChanged(true);
        }
    }


}
