
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
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Scaling;
import com.kotcrab.vis.ui.VisUI;
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
import com.quexten.ravtech.dk.packaging.Packager.TargetPlatform;
import com.quexten.ravtech.dk.packaging.platforms.BuildOptions;
import com.quexten.ravtech.dk.packaging.platforms.BuildOptions.AssetType;
import com.quexten.ravtech.dk.packaging.platforms.android.KeyStoreCredentials;
import com.quexten.ravtech.dk.ui.editor.RavWindow;

import se.vidstige.jadb.JadbDevice;

public class BuildDialog extends RavWindow {

	VisTable contentTable = new VisTable();

	public BuildDialog () {
		super("Build");
		add(contentTable).grow();

		setSize(500, 350);
		setVisible(true);
		addCloseButton();
		contentTable.add(createPlatformTable()).grow();
		setResizable(true);
		WindowStyle windowStyle = getStyle();
		WindowStyle newWindowStyle = new WindowStyle();
		newWindowStyle.background = windowStyle.background;
		newWindowStyle.titleFont = windowStyle.titleFont;
		newWindowStyle.titleFontColor = windowStyle.titleFontColor;
		newWindowStyle.stageBackground = VisUI.getSkin()
			.getDrawable("dialogDim");
		setStyle(newWindowStyle);
		centerWindow();
	}

	public VisTable createPlatformTable () {
		VisTable contentTable = new VisTable();
		contentTable.setFillParent(true);

		final VisList<TargetPlatform> platformList = new VisList<TargetPlatform>();
		Array<TargetPlatform> platforms = new Array<TargetPlatform>();
		platforms.add(TargetPlatform.Desktop);
		platforms.add(TargetPlatform.Android);
		platforms.add(TargetPlatform.WebGL);
		platformList.setItems(platforms);
		contentTable.add(platformList).grow();

		final VisTable optionsTable = new VisTable();
		optionsTable.add(new VisLabel("AssetType")).growX().left();
		final VisSelectBox<String> dropDown = new VisSelectBox<String>();
		dropDown.setItems("Internal", "External");
		optionsTable.add(dropDown).growX();

		// Skip Build Box
		optionsTable.row();
		optionsTable.add(new VisLabel("Skip Build")).growX().left();
		final VisCheckBox skipBuildBox = new VisCheckBox("");
		optionsTable.add(skipBuildBox).left();

		// Sign Box
		optionsTable.row();
		optionsTable.add(new VisLabel("Sign")).growX().left();
		final VisCheckBox signBox = new VisCheckBox("");
		optionsTable.add(signBox).left();

		contentTable.add(optionsTable).growX().top();

		VisTable bottomTable = new VisTable();

		VisTextButton buildButton = new VisTextButton("Build");
		buildButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				if (platformList.getSelected()
					.equals(TargetPlatform.Android)) {
					if (signBox.isChecked()) {
						BuildDialog.this.contentTable.clear();
						BuildDialog.this.contentTable
							.add(createApkOptionsTable(new BuildOptions(
								(dropDown.getSelectedIndex() == 0)
									? AssetType.Internal : AssetType.External,
								skipBuildBox.isChecked())))
							.grow();
						return;
					}
				}

				// Build Options
				BuildOptions options = new BuildOptions(
					(dropDown.getSelectedIndex() == 0) ? AssetType.Internal
						: AssetType.External,
					skipBuildBox.isChecked());
				options.sign = signBox.isChecked();

				BuildDialog.this.build(platformList.getSelected(),
					options);
			}
		});
		bottomTable.add(buildButton);

		VisTextButton buildAndRunButton = new VisTextButton(
			"Build and Run");
		buildAndRunButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				if (platformList.getSelected()
					.equals(TargetPlatform.Android)) {
					BuildOptions options = new BuildOptions(
						(dropDown.getSelectedIndex() == 0) ? AssetType.Internal
							: AssetType.External,
						skipBuildBox.isChecked());
					options.sign = signBox.isChecked();
					
					BuildDialog.this.contentTable.clear();
					BuildDialog.this.contentTable.add(createDeviceSelector(options)).grow();
					return;
				}

				BuildOptions options = new BuildOptions(
					(dropDown.getSelectedIndex() == 0) ? AssetType.Internal
						: AssetType.External,
					skipBuildBox.isChecked());
				options.run = true;
				BuildDialog.this.build(platformList.getSelected(),
					options);
			}
		});
		bottomTable.add(buildAndRunButton);

		contentTable.row();
		contentTable.add(new Actor());
		contentTable.add(bottomTable).align(Align.right);
		return contentTable;
	}

	public VisTable createApkOptionsTable (
		final BuildOptions buildOptions) {
		final VisTable optionsTable = new VisTable();
		optionsTable.setFillParent(true);

		final VisTable contentTable = new VisTable();
		final VisTextField keystorePathField = new VisTextField();
		final VisTextButton keystoreSelectButton = new VisTextButton(
			"Select");
		final VisTextButton keystoreCreateTextButton = new VisTextButton(
			"Create");
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
				return Gdx.files.absolute(arg0.getAbsolutePath())
					.extension().equals("keystore") || arg0.isDirectory();
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
		contentTable.add(new VisLabel("Keystore Password:"))
			.align(Align.left);
		contentTable.add(keystorePasswordField).growX();

		// KeyStoreAlias
		contentTable.row();
		contentTable.add(new VisLabel("Alias:")).align(Align.left);
		contentTable.add(aliasField).growX();

		// KeyStoreAliasPassword
		contentTable.row();
		contentTable.add(new VisLabel("Alias Password:"))
			.align(Align.left);
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
				buildOptions.credentials = new KeyStoreCredentials(
					new File(keystorePathField.getText()),
					keystorePasswordField.getText(), aliasField.getText(),
					aliasPasswordField.getText());
				buildOptions.sign = true;
				BuildDialog.this.build(TargetPlatform.Android,
					buildOptions);
			}
		});
		bottomTable.add(buildButton).align(Align.bottomRight);

		optionsTable.add(contentTable).grow();
		optionsTable.row();
		optionsTable.add(bottomTable).grow();
		return optionsTable;
	}

	public VisTable createDeviceSelector (final BuildOptions currentOptions) {
		RavTechDK.editorAssetManager
			.load("resources/ui/icons/no-device.png", Texture.class);
		RavTechDK.editorAssetManager.finishLoading();
		final Image noDeviceImage = new Image(
			(Texture)RavTechDK.editorAssetManager
				.get("resources/ui/icons/no-device.png"));
		noDeviceImage.setScaling(Scaling.fillY);

		final VisList<String> deviceList = new VisList<String>();
		fillList(deviceList);

		final VisTable contentTable = new VisTable();

		contentTable.add(
			deviceList.getItems().size == 0 ? noDeviceImage : deviceList)
			.grow();
		contentTable.row();

		final VisTable bottomTable = new VisTable();
		final VisLabel statusLabel = new VisLabel("");
		bottomTable.add(statusLabel);
		bottomTable.add().growX();
		
		final VisTextButton refreshButton = new VisTextButton(
			"Refresh");
		ChangeListener refreshListener = new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {				
				fillList(deviceList);
				contentTable.clear();
				contentTable.add(deviceList.getItems().size == 0
					? noDeviceImage : deviceList).grow();
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
				currentOptions.deviceId = deviceList.getSelected().substring(deviceList.getSelected().lastIndexOf(" ") + 1);
				BuildDialog.this.build(TargetPlatform.Android,
					currentOptions);				
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
			deviceStrings.add(AdbManager.getDeviceName(devices.get(i))
				+ " | " + devices.get(i).getSerial());
		}

		deviceList.setItems(deviceStrings);
	}

	public void build (TargetPlatform targetPlatform,
		BuildOptions options) {
		BuildReporterDialog buildDialog = new BuildReporterDialog();
		contentTable.clearChildren();
		VisScrollPane scrollPane = new VisScrollPane(buildDialog);
		contentTable.add(scrollPane).grow().pad(10).align(Align.top)
			.padTop(32);

		if (targetPlatform == TargetPlatform.Android && options.run) {
			if (!AdbManager.initialized) {
				com.quexten.ravtech.util.Debug.logError("Adb Error",
					"Adb Path Not Delcared");
				AdbManager.initializeAdb();
			}
		}

		Packager.build(buildDialog, targetPlatform, options);
	}

}
