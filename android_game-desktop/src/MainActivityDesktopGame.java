
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.me.android_game.GameMain;


public class MainActivityDesktopGame {
	
	
	public static void main(String[] args) {
		
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		
		cfg.title = "my-gdx-game";
	    cfg.useGL20 = false;
	    cfg.width = 800;
	    cfg.height = 800;
		
		new LwjglApplication(new GameMain(), cfg);
		
		

    }

}
