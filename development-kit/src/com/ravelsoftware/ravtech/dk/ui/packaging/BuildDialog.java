
package com.ravelsoftware.ravtech.dk.ui.packaging;

import java.io.File;
import java.io.FileFilter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisList;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.VisWindow;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.FileChooser.Mode;
import com.kotcrab.vis.ui.widget.file.FileChooser.SelectionMode;
import com.kotcrab.vis.ui.widget.file.FileChooserAdapter;
import com.ravelsoftware.ravtech.dk.RavTechDK;
import com.ravelsoftware.ravtech.dk.adb.AdbManager;
import com.ravelsoftware.ravtech.dk.packaging.Packager;
import com.ravelsoftware.ravtech.dk.packaging.Packager.TargetPlatform;
import com.ravelsoftware.ravtech.dk.packaging.platforms.BuildOptions;
import com.ravelsoftware.ravtech.dk.packaging.platforms.BuildOptions.AssetType;
import com.ravelsoftware.ravtech.dk.packaging.platforms.android.KeyStoreCredentials;

import se.vidstige.jadb.JadbDevice;

public class BuildDialog extends VisWindow {

	VisTable contentTable = new VisTable();

	public BuildDialog () {
		super("Build");
		contentTable.setFillParent(true);
		addActor(contentTable);
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
		newWindowStyle.stageBackground = VisUI.getSkin().getDrawable("dialogDim");
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
		contentTable.padTop(28).padLeft(20);
		contentTable.add(platformList).grow();

		final VisTable optionsTable = new VisTable();
		optionsTable.add(new VisLabel("AssetType"));
		final VisSelectBox<String> dropDown = new VisSelectBox<String>();
		dropDown.setItems("Internal", "External");
		optionsTable.add(dropDown);
		contentTable.add(optionsTable).grow();
		
		VisTable bottomTable = new VisTable();

		VisTextButton buildButton = new VisTextButton("Build");
		buildButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				if (platformList.getSelected().equals(TargetPlatform.Android)) {
					BuildDialog.this.contentTable.clear();
					BuildDialog.this.contentTable.add(createApkOptionsTable(
						new BuildOptions((dropDown.getSelectedIndex() == 0) ? AssetType.Internal : AssetType.External))).grow();
					return;
				}
				BuildDialog.this.build(platformList.getSelected(), false,
					new BuildOptions((dropDown.getSelectedIndex() == 0) ? AssetType.Internal : AssetType.External));
			}
		});
		bottomTable.add(buildButton);

		VisTextButton buildAndRunButton = new VisTextButton("Build and Run");
		buildAndRunButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				if (platformList.getSelected().equals(TargetPlatform.Android) && AdbManager.getDevices().size == 0)
					return;
				if (platformList.getSelected().equals(TargetPlatform.Android) && AdbManager.getDevices().size > 1)
					return;
				BuildDialog.this.build(platformList.getSelected(), true,
					new BuildOptions((dropDown.getSelectedIndex() == 0) ? AssetType.Internal : AssetType.External));
			}
		});
		bottomTable.add(buildAndRunButton);

		contentTable.row();
		contentTable.add(bottomTable).align(Align.right);
		return contentTable;
	}

	public VisTable createApkOptionsTable (final BuildOptions buildOptions) {
		VisTable optionsTable = new VisTable();
		optionsTable.setFillParent(true);

		VisTable contentTable = new VisTable();
		final VisTextField keystorePathField = new VisTextField();
		VisTextButton keystoreSelectButton = new VisTextButton("Select");
		VisTextButton keystoreCreateTextButton = new VisTextButton("Create");
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

		contentTable.add(new VisLabel("Keystore:")).align(Align.left);
		VisTable keystoreTable = new VisTable();
		keystoreTable.add(keystorePathField).growX();
		keystoreTable.add(keystoreSelectButton).align(Align.right);
		contentTable.add(keystoreTable).growX();
		contentTable.row();
		contentTable.add(new Actor());
		contentTable.add(keystoreCreateTextButton).align(Align.right);
		contentTable.row();
		contentTable.add(new VisLabel("Keystore Password:")).align(Align.left);
		contentTable.add(keystorePasswordField).growX();
		contentTable.row();
		contentTable.add(new VisLabel("Alias:")).align(Align.left);
		contentTable.add(aliasField).growX();
		contentTable.row();
		contentTable.add(new VisLabel("Alias Password:")).align(Align.left);
		contentTable.add(aliasPasswordField).growX();
		contentTable.row();

		contentTable.pad(10);
		contentTable.align(Align.top);
		contentTable.padTop(32);

		VisTable bottomTable = new VisTable();
		bottomTable.align(Align.bottomRight).pad(10);
		VisTextButton buildButton = new VisTextButton("Build");
		buildButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				BuildDialog.this.build(TargetPlatform.Android, false, new KeyStoreCredentials(new File(keystorePathField.getText()),
					keystorePasswordField.getText(), aliasField.getText(), aliasPasswordField.getText()), buildOptions);
			}
		});
		bottomTable.add(buildButton).align(Align.bottomRight);
		optionsTable.add(contentTable).grow();
		optionsTable.row();
		optionsTable.add(bottomTable).grow();

		return optionsTable;
	}

	public void build (TargetPlatform targetPlatform, boolean run, Object userData, BuildOptions options) {
		BuildReporterDialog buildDialog = new BuildReporterDialog();
		contentTable.clearChildren();
		VisScrollPane scrollPane = new VisScrollPane(buildDialog);
		contentTable.add(scrollPane).grow().pad(10).align(Align.top).padTop(32);

		if (targetPlatform == TargetPlatform.Android && run) {
			if (!AdbManager.initialized) {
				com.ravelsoftware.ravtech.util.Debug.logError("Adb Error", "Adb Path Not Delcared");
				AdbManager.initializeAdb();
			}
			Array<JadbDevice> devices = AdbManager.getDevices();
			if (devices.size == 1)
				Packager.run(buildDialog, targetPlatform, "");
			else if (devices.size > 1)
				// show device selector
				return;
		}

		if (run)
			Packager.run(buildDialog, targetPlatform, "");
		else
			Packager.dist(buildDialog, targetPlatform, userData, getDistFileHandle(targetPlatform),
				new BuildOptions(AssetType.Internal));
	}

	public void build (TargetPlatform targetPlatform, boolean run, BuildOptions buildOptions) {
		this.build(targetPlatform, run, null, buildOptions);
	}

	FileHandle getDistFileHandle (TargetPlatform platform) {
		FileHandle buildsHandle = RavTechDK.projectHandle.child("builds");
		switch (platform) {
		case Android:
			return buildsHandle.child("android");
		case Desktop:
			return buildsHandle.child("desktop");
		case Linux:
			break;
		case Mac:
			break;
		case WebGL:
			break;
		case Windows:
			break;
		case iOS:
			break;
		default:
			break;
		}
		return buildsHandle.child("error");
	}

}
