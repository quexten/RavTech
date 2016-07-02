
package com.quexten.ravtech.dk.ui.packaging;

import java.io.File;
import java.io.FileFilter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisList;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.FileChooser.Mode;
import com.kotcrab.vis.ui.widget.file.FileChooser.SelectionMode;
import com.kotcrab.vis.ui.widget.file.FileChooserAdapter;
import com.quexten.ravtech.dk.RavTechDK;
import com.quexten.ravtech.dk.adb.AdbManager;
import com.quexten.ravtech.dk.packaging.Packager;
import com.quexten.ravtech.dk.packaging.platforms.AndroidBuildOptions;
import com.quexten.ravtech.dk.packaging.platforms.BuildOptions;
import com.quexten.ravtech.dk.packaging.platforms.BuildOptions.AssetType;
import com.quexten.ravtech.dk.ui.editor.RavWindow;

import se.vidstige.jadb.JadbDevice;

public class BuildDialog extends RavWindow {

	VisTable contentTable = new VisTable();
	BuildOptions options = new AndroidBuildOptions(AssetType.Internal);
	VisTable platformTable;

	public BuildDialog () {
		super("Build");
		add(contentTable).grow();

		contentTable.add(createPlatformTable()).grow();

		setSize(500, 350);
		setVisible(true);
		setResizable(true);
		addCloseButton();

		centerWindow();
	}

	@SuppressWarnings("unchecked")
	public VisTable createPlatformTable () {

		final VisTable contentTable = new VisTable();

		final VisList<String> platformList = new VisList<String>();
		platformList.setItems(Packager.getPlatforms());
		contentTable.add(platformList).grow();

		// Options Table
		final VisTable optionsTable = new VisTable();
		final VisSelectBox<String> dropDown = new VisSelectBox<String>();
		dropDown.setItems("Internal", "External");
		dropDown.setSelected(options.assetType.toString());
		optionsTable.add(new VisLabel("AssetType")).growX().left();
		optionsTable.add(dropDown).growX();
		optionsTable.row();

		// Skip Build Box
		final VisCheckBox skipBuildBox = new VisCheckBox("");
		skipBuildBox.setDisabled(options.assetType.equals(AssetType.Internal));
		skipBuildBox.setChecked(options.skipBuild);
		optionsTable.add(new VisLabel("Skip Build")).growX().left();
		optionsTable.add(skipBuildBox).left();
		optionsTable.row();

		// DropDown Listener
		dropDown.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				if (dropDown.getSelected().equals("Internal"))
					skipBuildBox.setChecked(false);
				skipBuildBox.setDisabled(dropDown.getSelected().equals("Internal"));
			}
		});

		contentTable.add(optionsTable).growX().top();
		contentTable.row();

		final VisTable platformPaddingTable = new VisTable();
		platformTable = Packager.getPlatform(options.targetPlatform).getOptionsTable(options);
		platformPaddingTable.add(platformTable).growX();
		optionsTable.add(platformPaddingTable).colspan(2).growX();
		contentTable.row();

		platformList.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				BuildOptions oldOptions = options;
				options = Packager.getPlatform(platformList.getSelected()).getOptions();
				oldOptions.copyTo(options);
				platformPaddingTable.clear();
				platformTable = Packager.getPlatform(platformList.getSelected()).getOptionsTable(options);
				platformPaddingTable.add(platformTable).growX();
			}
		});

		// Bottom Table
		final VisTable bottomTable = new VisTable();
		final VisTextButton buildButton = new VisTextButton("Build");
		buildButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				options.skipBuild = skipBuildBox.isChecked();
				com.quexten.ravtech.util.Debug.log("Platform", options.targetPlatform);
				Packager.getPlatform(options.targetPlatform).applyOptions(platformTable, options);
				BuildDialog.this.build(options);
			}
		});
		bottomTable.add(buildButton);

		final VisTextButton buildAndRunButton = new VisTextButton("Build and Run");
		buildAndRunButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				options.run = true;
				buildButton.getListeners().peek().handle(new ChangeEvent());
			}
		});
		bottomTable.add(buildAndRunButton);

		contentTable.add(new Actor());
		contentTable.add(bottomTable).align(Align.right);

		return contentTable;
	}

	public VisTable createApkOptionsTable (final BuildOptions buildOptions) {
		final VisTable optionsTable = new VisTable();
		optionsTable.setFillParent(true);

		final VisTable contentTable = new VisTable();
		final VisTextField keystorePathField = new VisTextField();
		final VisTextButton keystoreSelectButton = new VisTextButton("Select");
		final VisTextButton keystoreCreateTextButton = new VisTextButton("Create");
		final VisTextField keystorePasswordField = new VisTextField();
		keystorePasswordField.setPasswordMode(true);
		final VisTextField aliasField = new VisTextField();
		final VisTextField aliasPasswordField = new VisTextField();
		aliasPasswordField.setPasswordMode(true);

		final FileChooser fileChooser = new FileChooser(Mode.OPEN);
		fileChooser.setSelectionMode(SelectionMode.FILES);
		fileChooser.setFileFilter(new FileFilter() {
			@Override
			public boolean accept (File arg0) {
				return Gdx.files.absolute(arg0.getAbsolutePath()).extension().equals("keystore") || arg0.isDirectory();
			}
		});
		fileChooser.setListener(new FileChooserAdapter() {
			@Override
			public void selected (Array<FileHandle> file) {
				keystorePathField.setText(file.first().path());
			}
		});
		keystoreSelectButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				getStage().addActor(fileChooser.fadeIn());
			}
		});

		// KeyStoreSelect
		contentTable.add(new VisLabel("Keystore:")).align(Align.left);
		VisTable keystoreTable = new VisTable();
		keystoreTable.add(keystorePathField).growX();
		keystoreTable.add(keystoreSelectButton).align(Align.right);
		contentTable.add(keystoreTable).growX();

		// KeyStoreCreate
		contentTable.row();
		contentTable.add(new Actor());
		contentTable.add(keystoreCreateTextButton).align(Align.right);

		// KeyStorePassword
		contentTable.row();
		contentTable.add(new VisLabel("Keystore Password:")).align(Align.left);
		contentTable.add(keystorePasswordField).growX();

		// KeyStoreAlias
		contentTable.row();
		contentTable.add(new VisLabel("Alias:")).align(Align.left);
		contentTable.add(aliasField).growX();

		// KeyStoreAliasPassword
		contentTable.row();
		contentTable.add(new VisLabel("Alias Password:")).align(Align.left);
		contentTable.add(aliasPasswordField).growX();

		// Padding
		contentTable.row();
		contentTable.pad(10);
		contentTable.align(Align.top);
		contentTable.padTop(32);

		VisTable bottomTable = new VisTable();
		bottomTable.align(Align.bottomRight).pad(10);

		// BuildButton
		VisTextButton buildButton = new VisTextButton("Build");
		buildButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				/*
				 * buildOptions.credentials = new KeyStoreCredentials( new File(keystorePathField.getText()),
				 * keystorePasswordField.getText(), aliasField.getText(), aliasPasswordField.getText()); buildOptions.sign = true;
				 * BuildDialog.this.build(buildOptions);
				 */
			}
		});
		bottomTable.add(buildButton).align(Align.bottomRight);

		optionsTable.add(contentTable).grow();
		optionsTable.row();
		optionsTable.add(bottomTable).grow();
		return optionsTable;
	}

	public VisTable createDeviceSelector (final BuildOptions currentOptions) {
		RavTechDK.editorAssetManager.load("resources/ui/icons/no-device.png", Texture.class);
		RavTechDK.editorAssetManager.finishLoading();
		final Image noDeviceImage = new Image((Texture)RavTechDK.editorAssetManager.get("resources/ui/icons/no-device.png"));
		noDeviceImage.setScaling(Scaling.fillY);

		final VisList<String> deviceList = new VisList<String>();
		fillList(deviceList);

		final VisTable contentTable = new VisTable();

		contentTable.add(deviceList.getItems().size == 0 ? noDeviceImage : deviceList).grow();
		contentTable.row();

		final VisTable bottomTable = new VisTable();
		final VisLabel statusLabel = new VisLabel("");
		bottomTable.add(statusLabel);
		bottomTable.add().growX();

		final VisTextButton refreshButton = new VisTextButton("Refresh");
		ChangeListener refreshListener = new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				fillList(deviceList);
				contentTable.clear();
				contentTable.add(deviceList.getItems().size == 0 ? noDeviceImage : deviceList).grow();
				contentTable.row();
				contentTable.add(bottomTable).fillX();
				statusLabel.setText(deviceList.getItems().size == 0 ? "No devices plugged in." : "Select a device.");
			}
		};
		refreshListener.changed(null, null);
		refreshButton.addListener(refreshListener);

		bottomTable.add(refreshButton);
		VisTextButton nextButton = new VisTextButton("Next");
		nextButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				currentOptions.run = true;
				((AndroidBuildOptions) currentOptions).deviceId = deviceList.getSelected().substring(deviceList.getSelected().indexOf("|") + 2);
				BuildDialog.this.build(currentOptions);
			}
		});
		bottomTable.add(nextButton);
		contentTable.row();
		contentTable.add(bottomTable).fillX();

		return contentTable;
	}

	void fillList (VisList<String> deviceList) {
		deviceList.clear();
		Array<JadbDevice> devices = AdbManager.getDevices();
		Array<String> deviceStrings = new Array<String>();
		for (int i = 0; i < devices.size; i++) {
			deviceStrings.add(AdbManager.getDeviceName(devices.get(i)) + " | " + devices.get(i).getSerial());
		}

		deviceList.setItems(deviceStrings);
	}

	public void build (BuildOptions options) {
		if (options.targetPlatform == "Android" && options.run && ((AndroidBuildOptions) options).deviceId == null) {
			if (!AdbManager.initialized) {
				com.quexten.ravtech.util.Debug.logError("Adb Error", "Adb Path Not Delcared");
				AdbManager.initializeAdb();
			}	
			contentTable.clearChildren();
			contentTable.add(createDeviceSelector(options)).grow().pad(10).align(Align.top).padTop(32);
			return;
		}
		
		BuildReporterDialog buildDialog = new BuildReporterDialog();
		contentTable.clearChildren();
		VisScrollPane scrollPane = new VisScrollPane(buildDialog);
		contentTable.add(scrollPane).grow().pad(10).align(Align.top).padTop(32);
		Packager.build(buildDialog, options);
	}

}
