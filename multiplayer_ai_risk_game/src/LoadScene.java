import WarGUI.JogoScene;
import WarGUI.MenuScene;
import pulpcore.scene.LoadingScene;

public class LoadScene extends LoadingScene {
    
    public LoadScene() {
        super("LoadScene.zip" , new MenuScene());
    }

    @Override
    public void load() {
        super.load();
    }
}
