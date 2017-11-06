package com.me.android_game;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.utils.Json;

public class GameMain implements ApplicationListener {
	
	public static MyController myController;
	private ShapeRenderer apple;
	private Team[][] teams; 
	private float scale;
	private float screenHeight;
	private float screenWidth;
	TextButton undoButton;
	TextButton cantUndoButton;
	Stack stack;
	Preferences prefs;
	Skin skin;
	Stage stage;
	private String PREFS_BOARD_KEY = "BOARD";
	private String PREFS_PLAYER_TURN = "TURN";
	Label statusLabel;
	 
	@Override
	public void create() {
		prefs = Gdx.app.getPreferences("Preferences");
		
		InputMultiplexer multiplexer = new InputMultiplexer();
		UIButtonListener uiButtonListener = new UIButtonListener();
		screenHeight = Gdx.graphics.getHeight();
		screenWidth = Gdx.graphics.getWidth();
		
		if(screenHeight >= screenWidth){
			scale = (screenWidth/8);
		}
		else{
			scale = (screenHeight / 8);
		}
		
		apple = new ShapeRenderer();
		myController = new MyController(8, scale);
		teams = myController.getPiecesTeams();
		//////
		stage = new Stage();
		skin = new Skin();

        // Generate a 1x1 white texture and store it in the skin named "white".
        Pixmap pixmap = new Pixmap((int)scale,(int)scale, Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        skin.add("white", new Texture(pixmap));

        // Store the default libgdx font under the name "default".
        skin.add("default", new BitmapFont());

        // Configure a TextButtonStyle and name it "default". Skin resources are stored by type, so this doesn't overwrite the font.
        TextButtonStyle textButtonStyle = new TextButtonStyle();
        textButtonStyle.up = skin.newDrawable("white", Color.DARK_GRAY);
        textButtonStyle.down = skin.newDrawable("white", Color.GRAY);
        textButtonStyle.checked = skin.newDrawable("white", Color.DARK_GRAY);
        textButtonStyle.over = skin.newDrawable("white", Color.LIGHT_GRAY);
        BitmapFont font = new BitmapFont();
        font.setScale(1);
        textButtonStyle.font = font;
        skin.add("default", textButtonStyle);
        
		final TextButton button = new TextButton("Reset Board", skin);
		uiButtonListener.setButton(button);
		
		final TextButton saveAndQuitButton = new TextButton("Save and Quit", skin);
		uiButtonListener.setSaveAndQuitButton(saveAndQuitButton);
		
		cantUndoButton = new TextButton("Cannot undo previous move", skin);
		
		undoButton = new TextButton("Undo Last Move", skin);
		uiButtonListener.setUndoButton(undoButton);
		
		button.addListener(uiButtonListener);
		saveAndQuitButton.addListener(uiButtonListener);
		undoButton.addListener(uiButtonListener);
		
		// label
		LabelStyle labelStyle = new LabelStyle(font, Color.GREEN);
		statusLabel = new Label("", labelStyle);
		
		stack = new Stack();
		stack.add(cantUndoButton);
		stack.add(undoButton);
		
		Table table = new Table();
		table.add(stack).height((scale*1.5f)).expandY().padTop((scale * .5f)).prefWidth((screenWidth-screenHeight)-scale);
		table.row();
		table.add(button).height((scale*1.5f)).expandY().padBottom((scale * .5f)).padTop((scale * .5f)).prefWidth((screenWidth-screenHeight)-scale);
		table.row();
		table.add(saveAndQuitButton).height((scale*1.5f)).expandY().padBottom((scale * .5f)).prefWidth((screenWidth-screenHeight)-scale);
		table.row();
		table.add(statusLabel).height((scale*1.5f)).expandY().padBottom((scale * .5f)).prefWidth((screenWidth-screenHeight)-scale);
		
		table.setFillParent(false);
		table.setVisible(true);
		table.setSize((screenWidth-screenHeight), screenHeight);
		table.setPosition(screenHeight,0);
		
		stage.addActor(table);
		
		multiplexer.addProcessor(myController);
		multiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(multiplexer);
        
        String savedBoardJsonString = prefs.getString(PREFS_BOARD_KEY, null);
        int savedTurn = prefs.getInteger(PREFS_PLAYER_TURN);
        
        if(savedBoardJsonString != null){
        	myController.setBoard(savedBoardJsonString);
        	myController.setTurn(savedTurn);
        	
        	
        }
	}

	@Override
	public void dispose() {
	}

	@Override
	public void render() {
		
		 Gdx.graphics.getGL20().glClearColor( 0, 0, 0, 1 );
		 Gdx.graphics.getGL20().glClear( GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT );
		
		teams = myController.getPiecesTeams();
		
		if(myController.getCurrentTurnInt() == 1){
			statusLabel.setText("Red Player's Turn");
    	}
    	else{
    		statusLabel.setText("Blue Player's Turn");
    	}
		
		
		if(myController.undoLegal){
			cantUndoButton.setVisible(false);
			undoButton.setVisible(true);
		}
		else {
			cantUndoButton.setVisible(true);
			undoButton.setVisible(false);
		}
		
		apple.begin(ShapeType.Filled);
		
		for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                // sets checker board colors
                if (row % 2 == col % 2) {
                	apple.setColor(Color.LIGHT_GRAY);
                } else {
                	apple.setColor(Color.GRAY);
                }
                apple.rect((col* scale), (row * scale), scale, scale);
                // sets pieces on the board
                if (teams[row][col] == Team.TEAM1) {
                	apple.setColor(Color.RED);
                    apple.circle(0+(int)((col+0.5) * scale), 0+(int)((row+0.5) * scale), scale/4);
                } else if (teams[row][col] == Team.TEAM2) {
                	apple.setColor(Color.BLUE);
                    apple.circle(0+(int)((col+0.5) * scale), 0+(int)((row+0.5) * scale), scale/4);
                } else {
                    // neutral
                	apple.setColor(Color.WHITE);
                    apple.circle(0+(int)((col+0.5) * scale), 0+(int)((row+0.5) * scale), scale/4);
                }
            }
        }
		
		// checks for winner
		Team winner = myController.getWinner();
				if ( winner== Team.TEAM1){ // Red wins
					statusLabel.setText("Red Player Wins!");
				}
				else if(winner == Team.TEAM2){ // blue wins
					statusLabel.setText("Blue Player Wins!");
				}
				else if(winner == Team.NEUTRAL){ // tie
					statusLabel.setText("It's a Tie!");
				}
				else { // no winner
					
				}
		
		apple.end();
		stage.draw();
        Table.drawDebug(stage);
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
		Json json = new Json();
		teams = myController.getPiecesTeams();
        String savedTeamJson = json.toJson(teams);
        
		prefs.putString(PREFS_BOARD_KEY, savedTeamJson);
		
		prefs.putInteger(PREFS_PLAYER_TURN, myController.getCurrentTurnInt());
		
        prefs.flush();
	}
	
	// only on android
	@Override
	public void resume() {
		String savedBoardJsonString = prefs.getString(PREFS_BOARD_KEY, null);
		int savedTurn = prefs.getInteger(PREFS_PLAYER_TURN);
        
        if(savedBoardJsonString != null){
        	myController.setBoard(savedBoardJsonString);
        	myController.setTurn(savedTurn);
        }
	}
}