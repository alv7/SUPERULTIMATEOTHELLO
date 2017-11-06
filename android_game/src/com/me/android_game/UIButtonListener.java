package com.me.android_game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class UIButtonListener implements EventListener{
	
	TextButton button;
	TextButton saveAndQuitButton;
	TextButton undoButton;

	@Override
	public boolean handle(Event event) {
		
		if(button == event.getListenerActor()){
			GameMain.myController.resetBoard();
			GameMain.myController.resetTurn();
			return true;
		}
		else if(saveAndQuitButton == event.getListenerActor()){
			Gdx.app.exit();
			return true;
		}
		else if(undoButton == event.getListenerActor()){
			if(GameMain.myController.undoLegal){
				GameMain.myController.undoMove();
			}
			return true;
		}
		else{
			return false;
		}
	}
	
	public void setButton(TextButton button){
		this.button = button;
	}
	
	public void setSaveAndQuitButton(TextButton button){
		this.saveAndQuitButton = button;
	}
	
	public void setUndoButton(TextButton button){
		this.undoButton = button;
	}

}
