
package com.ravelsoftware.ravtech.dk.ui.animation;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Line2D;
import java.awt.geom.QuadCurve2D;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import com.brashmonkey.spriter.Curve;

public class CurveEditor extends JFrame {

	/**
	 *
	 */
	private static final long serialVersionUID = -3943999222161208110L;
	JPanel currentOptionsPanel;
	Curve curve = new Curve(com.brashmonkey.spriter.Curve.Type.Linear);
	int draggedPointIndex = 0;
	float maxTime = 1.5f;
	float[] points = new float[0];
	float time = 0;

	public CurveEditor () {
		setCurveType(com.brashmonkey.spriter.Curve.Type.Quartic);
		this.setSize(new Dimension(600, 440));
		final JPanel graphPanel = new JPanel() {

			/**
			 *
			 */
			private static final long serialVersionUID = 1603054229079574787L;

			@Override
			public void paintComponent (Graphics g) {
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D)g;
				{
					AffineTransform transform = AffineTransform.getTranslateInstance(0, getHeight());
					transform.scale(1, -1);
					g2.setTransform(transform);
				}
				int lineCount = 8;
				g2.setColor(Color.LIGHT_GRAY);
				for (int i = 0; i < lineCount; i++) {
					int lineX = Math.round((float)getWidth() / (float)lineCount) * i;
					g2.drawLine(lineX, 0, lineX, getHeight());
				}
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setStroke(new BasicStroke(2f));
				g2.setColor(Color.RED);
				switch (curve.getType()) {
				case Instant:
					g2.drawLine(0, 1, getWidth(), 1);
					g2.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight());
					break;
				case Linear:
					g2.drawLine(0, 0, getWidth(), getHeight());
					break;
				case Quadratic:
					QuadCurve2D.Float quadCurve = new QuadCurve2D.Float(0, 0, getWidth() / 2, points[0] * getHeight(), getWidth(),
						getHeight());
					g2.draw(quadCurve);
					break;
				case Cubic:
					CubicCurve2D.Float cubicCurve = new CubicCurve2D.Float(0, 0, getWidth() / 3, points[0] * getHeight(),
						2 * getWidth() / 3, points[1] * getHeight(), getWidth(), getHeight());
					g2.draw(cubicCurve);
					break;
				default:
					// g2.setColor(RavTechDKUtil.getAccentColor());
					float lastX = 0, lastY = 0;
					for (float i = 0; i <= 1.01; i += 0.04) {
						float newY = curve.tween(0, 1, i);
						if (i > 1f) newY = 1;
						float newX = i;
						g2.draw(new Line2D.Float(new Point(Math.round(lastX * getWidth()), Math.round(lastY * getHeight())),
							new Point(Math.round(newX * getWidth()), Math.round(newY * getHeight()))));
						lastX = newX;
						lastY = newY;
					}
					break;
				}
				int radius = 5;
				for (int i = 0; i < points.length; i++)
					g2.fillOval(Math.round(1f / (points.length + 1) * (i + 1) * getWidth() - radius + 1),
						Math.round(points[i] * getHeight() - radius), 2 * radius, 2 * radius);
				g2.setStroke(new BasicStroke(1));
				g2.setColor(Color.LIGHT_GRAY);
				if (time <= 1.0f) {
					g2.drawLine(Math.round(time * getWidth()), 0, Math.round(time * getWidth()), getHeight());
					g2.drawLine(0, Math.round(curve.tween(0, 1, time) * getHeight()), getWidth(),
						Math.round(curve.tween(0, 1, time) * getHeight()));
					g2.setColor(Color.RED);
					g2.fillOval(Math.round(time * getWidth() - radius), Math.round(curve.tween(0, 1, time) * getHeight() - radius),
						2 * radius, 2 * radius);
				}
			}
		};
		graphPanel.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged (MouseEvent arg0) {
				int mouseY = graphPanel.getHeight() - arg0.getY();
				mouseY = Math.min(mouseY, graphPanel.getHeight());
				mouseY = Math.max(mouseY, 0);
				float height = (float)mouseY / (float)graphPanel.getHeight();
				points[draggedPointIndex] = height;
				switch (draggedPointIndex) {
				case 0:
					curve.constraints.c1 = height;
					break;
				case 1:
					curve.constraints.c2 = height;
					break;
				case 2:
					curve.constraints.c3 = height;
					break;
				case 3:
					curve.constraints.c4 = height;
					break;
				}
			}

			@Override
			public void mouseMoved (MouseEvent arg0) {
			}
		});
		graphPanel.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked (MouseEvent arg0) {
			}

			@Override
			public void mouseEntered (MouseEvent arg0) {
			}

			@Override
			public void mouseExited (MouseEvent arg0) {
			}

			@Override
			public void mousePressed (MouseEvent arg0) {
				draggedPointIndex = getClosestPointIndex(arg0.getX());
			}

			@Override
			public void mouseReleased (MouseEvent arg0) {
			}
		});
		GridBagConstraints constraints = new GridBagConstraints();
		this.setLayout(new GridBagLayout());
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 1;
		constraints.weighty = 1;
		graphPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		this.add(graphPanel, constraints);
		setupOptionsPanel(constraints);
		new Timer(16, new ActionListener() {

			@Override
			public void actionPerformed (ActionEvent evt) {
				time += 0.008;
				if (time > 1.5) time = 0;
				graphPanel.repaint();
			}
		}).start();
	}

	private int getClosestPointIndex (int mouseX) {
		if (points.length < 2) return 0;
		int closestIndex = 0;
		int closestDst = 9999999;
		for (int i = 0; i < points.length; i++) {
			int pointX = Math.round(1f / (points.length + 1) * (i + 1) * getWidth());
			int dst = Math.abs(mouseX - pointX);
			if (dst < closestDst) {
				closestIndex = i;
				closestDst = dst;
			}
		}
		return closestIndex;
	}

	public void setCurve (Curve curve) {
		this.curve = curve;
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 1;
		constraints.weighty = 1;
		setupOptionsPanel(constraints);
	}

	public void setCurveType (com.brashmonkey.spriter.Curve.Type type) {
		curve.setType(type);
		switch (curve.getType()) {
		case Instant:
			points = new float[] {};
			break;
		case Linear:
			points = new float[] {};
			break;
		case Quadratic:
			points = new float[] {0};
			break;
		case Cubic:
			points = new float[] {0, 0};
			break;
		case Quartic:
			points = new float[] {0, 0, 0};
			break;
		case Quintic:
			points = new float[] {0, 0, 0, 0};
			break;
		case Bezier:
			break;
		default:
			break;
		}
	}

	private void setupOptionsPanel (GridBagConstraints constraints) {
		if (currentOptionsPanel != null) {
			Container parent = currentOptionsPanel.getParent();
			parent.remove(currentOptionsPanel);
		}
		constraints.gridy++;
		double checkBoxWeight = 0.02;
		double labelWeight = 1.6;
		JPanel optionsPanel = new JPanel();
		// optionsPanel.setBackground(Color.RED);
		optionsPanel.setLayout(new GridBagLayout());
		GridBagConstraints optionsConstraints = new GridBagConstraints();
		optionsConstraints.gridx = 0;
		optionsConstraints.gridy = 0;
		optionsConstraints.weightx = checkBoxWeight;
		optionsConstraints.weighty = 1;
		optionsConstraints.insets = new Insets(4, 4, 4, 4);
		optionsConstraints.anchor = GridBagConstraints.NORTHWEST;
		ButtonGroup buttonGroup = new ButtonGroup();
		JCheckBox instantCheckBox, linearCheckBox;
		{ // First line (Instant / Linear)
			instantCheckBox = new JCheckBox();
			optionsPanel.add(instantCheckBox, optionsConstraints);
			buttonGroup.add(instantCheckBox);
			optionsConstraints.weightx = labelWeight;
			optionsConstraints.gridx++;
			JLabel instantLabel = new JLabel("Instant");
			instantLabel.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
			instantLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
			optionsPanel.add(instantLabel, optionsConstraints);
			optionsConstraints.weightx = checkBoxWeight;
			optionsConstraints.gridx++;
			linearCheckBox = new JCheckBox();
			optionsPanel.add(linearCheckBox, optionsConstraints);
			buttonGroup.add(linearCheckBox);
			optionsConstraints.weightx = labelWeight;
			optionsConstraints.gridx++;
			JLabel linearLabel = new JLabel("Linear");
			linearLabel.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
			linearLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
			optionsPanel.add(linearLabel, optionsConstraints);
			optionsConstraints.weightx = checkBoxWeight;
			optionsConstraints.gridx++;
		}
		optionsConstraints.gridx = 0;
		optionsConstraints.gridy++;
		JCheckBox curveCheckBox, bezierCheckBox;
		{ // Second Line (1-D Curve (Faster) / Bezier (Slower))
			curveCheckBox = new JCheckBox();
			optionsPanel.add(curveCheckBox, optionsConstraints);
			buttonGroup.add(curveCheckBox);
			optionsConstraints.weightx = labelWeight;
			optionsConstraints.gridx++;
			JLabel curveLabel = new JLabel("1-D Curve (Faster)");
			curveLabel.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
			curveLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
			optionsPanel.add(curveLabel, optionsConstraints);
			optionsConstraints.weightx = checkBoxWeight;
			optionsConstraints.gridx++;
			bezierCheckBox = new JCheckBox();
			optionsPanel.add(bezierCheckBox, optionsConstraints);
			buttonGroup.add(bezierCheckBox);
			optionsConstraints.weightx = labelWeight;
			optionsConstraints.gridx++;
			JLabel bezierLabel = new JLabel("Bezier (Slower)");
			bezierLabel.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
			bezierLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
			optionsPanel.add(bezierLabel, optionsConstraints);
			optionsConstraints.weightx = 1;
			optionsConstraints.gridx++;
		}
		optionsConstraints.gridx = 0;
		optionsConstraints.gridy++;
		{
			optionsConstraints.weightx = 1;
			optionsConstraints.weighty = 6;
			optionsConstraints.gridwidth = 4;
			optionsConstraints.fill = GridBagConstraints.BOTH;
			final JPanel valuePanel = new JPanel();
			instantCheckBox.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed (ActionEvent event) {
					CurveEditor.this.setCurveType(com.brashmonkey.spriter.Curve.Type.Instant);
					valuePanel.removeAll();
					valuePanel.repaint();
				}
			});
			linearCheckBox.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed (ActionEvent event) {
					CurveEditor.this.setCurveType(com.brashmonkey.spriter.Curve.Type.Linear);
					valuePanel.removeAll();
					valuePanel.repaint();
				}
			});
			curveCheckBox.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed (ActionEvent event) {
					valuePanel.removeAll();
					valuePanel.revalidate();
					valuePanel.setLayout(new GridBagLayout());
					GridBagConstraints curveConstraints = new GridBagConstraints();
					curveConstraints.gridx = 0;
					curveConstraints.gridy = 0;
					curveConstraints.fill = GridBagConstraints.HORIZONTAL;
					curveConstraints.weightx = 1;
					ButtonGroup buttonGroup = new ButtonGroup();
					JLabel label = createLabel("Quadratic");
					valuePanel.add(label, curveConstraints);
					curveConstraints.gridx++;
					JCheckBox checkBox = new JCheckBox();
					buttonGroup.add(checkBox);
					checkBox.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed (ActionEvent event) {
							CurveEditor.this.setCurveType(com.brashmonkey.spriter.Curve.Type.Quadratic);
						}
					});
					valuePanel.add(checkBox, curveConstraints);
					curveConstraints.gridx++;
					label = createLabel("Cubic");
					valuePanel.add(label, curveConstraints);
					curveConstraints.gridx++;
					checkBox = new JCheckBox();
					buttonGroup.add(checkBox);
					checkBox.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed (ActionEvent event) {
							CurveEditor.this.setCurveType(com.brashmonkey.spriter.Curve.Type.Cubic);
						}
					});
					valuePanel.add(checkBox, curveConstraints);
					curveConstraints.gridx++;
					label = createLabel("Quartic");
					valuePanel.add(label, curveConstraints);
					curveConstraints.gridx++;
					checkBox = new JCheckBox();
					buttonGroup.add(checkBox);
					checkBox.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed (ActionEvent event) {
							CurveEditor.this.setCurveType(com.brashmonkey.spriter.Curve.Type.Quartic);
						}
					});
					valuePanel.add(checkBox, curveConstraints);
					curveConstraints.gridx++;
					label = createLabel("Quintic");
					valuePanel.add(label, curveConstraints);
					curveConstraints.gridx++;
					checkBox = new JCheckBox();
					buttonGroup.add(checkBox);
					checkBox.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed (ActionEvent event) {
							CurveEditor.this.setCurveType(com.brashmonkey.spriter.Curve.Type.Quintic);
						}
					});
					valuePanel.add(checkBox, curveConstraints);
					valuePanel.repaint();
				}

				JLabel createLabel (String text) {
					JLabel label = new JLabel(text);
					label.setFont(new Font("SansSerif", Font.BOLD, 12));
					return label;
				}
			});
			bezierCheckBox.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed (ActionEvent event) {
					valuePanel.removeAll();
					valuePanel.add(new JLabel("Not Implemented"));
					valuePanel.repaint();
				}
			});
			valuePanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
			optionsPanel.add(valuePanel, optionsConstraints);
			for (int i = 0; i < curveCheckBox.getActionListeners().length; i++)
				curveCheckBox.getActionListeners()[i].actionPerformed(null);
			curveCheckBox.setSelected(true);
			valuePanel.revalidate();
			valuePanel.repaint();
		}
		this.add(optionsPanel, constraints);
		optionsPanel.revalidate();
		this.currentOptionsPanel = optionsPanel;
	}
}
