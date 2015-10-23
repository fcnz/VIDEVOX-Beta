package nz.ac.auckland.view;

import java.io.IOException;

import org.apache.log4j.Logger;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventDispatcher;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import nz.ac.auckland.application.VidevoxApplication;
import nz.ac.auckland.application.VidevoxPlayer;
import nz.ac.auckland.model.VidevoxException;

/**
 *
 * @author Fraser
 *
 */
public class PlayerViewController extends VIDEVOXController {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(PlayerViewController.class);

	@FXML
	private MediaView _mainPlayerView;

	@FXML
	private ImageView _playButton;

	@FXML
	private ImageView _pauseButton;

	@FXML
	private ImageView _skipBackButton;

	@FXML
	private ImageView _skipForwardButton;

	@FXML
	private VBox _scrollBox;

	@FXML
	private HBox _mediaControls;

	/**
	 * At this point, everything else should be ready to go so it can be used as
	 * trigger for setting up the view.
	 */
	@Override
	public void setMainApp(VidevoxApplication app) {
		super.setMainApp(app);
		logger.debug(getClass().getClassLoader().getResource("icons/play-icon.png").toString());
		// Set images for buttons
		_playButton.setImage(new Image(getClass().getClassLoader().getResource("icons/play-icon.png").toString()));
		_pauseButton.setImage(new Image(getClass().getClassLoader().getResource("icons/pause-icon.png").toString()));
		_skipForwardButton
				.setImage(new Image(getClass().getClassLoader().getResource("icons/last-track-icon.png").toString()));
		_skipBackButton
				.setImage(new Image(getClass().getClassLoader().getResource("icons/first-track-icon.png").toString()));
		MediaPlayer p = VidevoxPlayer.getPlayer().getMediaPlayer();
		if (p != null) {
			_mainPlayerView.setMediaPlayer(p);
			resize();
			// Add listeners for window size
			_application.getStage().heightProperty().addListener(new InvalidationListener() {
				@Override
				public void invalidated(Observable o) {
					resize();
				}
			});
			_application.getStage().widthProperty().addListener(new InvalidationListener() {
				@Override
				public void invalidated(Observable o) {
					resize();
				}
			});
		} else {
			_mediaControls.setDisable(true);
		}
	}

	private void resize() {
		// Figure out scale
		double mediaHeight = _mainPlayerView.getFitHeight();
		double mediaWidth = _mainPlayerView.getFitWidth();
		double fitHeight = _application.getStage().getHeight() - 160;
		double fitWidth = _application.getStage().getWidth() - 15;
		double scale;
		if (fitHeight / mediaHeight < fitWidth / mediaWidth) {
			scale = fitHeight / mediaHeight;
		} else {
			scale = fitWidth / mediaWidth;
		}
		logger.debug("Height scale = " + fitHeight / mediaHeight + ", fitHeight = " + fitHeight + ", mediaHeight"
				+ mediaHeight);
		logger.debug(
				"Width scale = " + fitWidth / mediaWidth + ", fitWidth = " + fitWidth + ", mediaWidth" + mediaWidth);

// _mainPlayerView.heigh
		_mainPlayerView.setScaleX(scale);
		_mainPlayerView.setScaleY(scale);
	}

	@FXML
	private void play() {
		VidevoxPlayer.getPlayer().play();
	}

	@FXML
	private void pause() {
		VidevoxPlayer.getPlayer().pause();
	}

	@FXML
	private void skipBack() {
		VidevoxPlayer.getPlayer().skipBack();
	}

	@FXML
	private void skipForward() {
		VidevoxPlayer.getPlayer().skipForward();
	}

	@FXML
	private void tts() {
		try {
			logger.trace("entered tts");

			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(this.getClass().getClassLoader().getResource("nz/ac/auckland/view/TTSView.fxml"));
			logger.debug("location: " + this.getClass().getClassLoader().getResource("nz/ac/auckland/view/TTSView.fxml"));
			VBox ttsView = (VBox) loader.load();

			logger.trace("Loaded ttsView from fxml");

			Stage stage = new Stage();
			stage.setTitle("VIDEVOX Text-to-Speech");
			stage.setScene(new Scene(ttsView));
			stage.setAlwaysOnTop(true);
			EventDispatcher ev = _application.getStage().getEventDispatcher();

			_application.getStage().setEventDispatcher(new EventDispatcher() {
				@Override
				public Event dispatchEvent(Event event, EventDispatchChain tail) {
					stage.requestFocus();
					return null;
				}
			});
			stage.showAndWait();
			_application.getStage().setEventDispatcher(ev);

			logger.trace("Showing ttsView");

			TTSViewController controller = loader.getController();
			controller.setMainApp(_application);

			_application.reset();

		} catch (IOException e) {
			logger.debug("error: " + e.getMessage());
			e.printStackTrace();
			VidevoxApplication.showExceptionDialog(new VidevoxException(e.getMessage()));
		}
	}

}
