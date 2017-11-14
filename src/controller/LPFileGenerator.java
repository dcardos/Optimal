package controller;

import javafx.stage.FileChooser;

import java.io.File;

public class LPFileGenerator {
    private final FileChooser mFileChooser = new FileChooser();

    public void saveLPFile(MainWindowController mwc) {
        mFileChooser.setTitle("Save lp file");
        mFileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("LP", "*.lp")
        );
        File file = mFileChooser.showSaveDialog(mwc.mMain.mPrimaryStage);
        if (file != null) {
//            try {
//                System.out.println("writing file");
//            } catch (IOException ex) {
//                System.out.println(ex.getMessage());
//            }
        }
    }
}
