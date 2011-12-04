package ets.log120.tp4.gui;

import ets.log120.tp4.app.Perspective;

public class PerspectiveTextualView extends javax.swing.JPanel {

	// --------------------------------------------------
	// Constructeur(s)
	// --------------------------------------------------

	public PerspectiveTextualView() {
		super(new java.awt.GridBagLayout());
		this.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));

		addProperty(0, 0, "Nom :", nameField = new javax.swing.JTextField("test"));
		addProperty(1, 0, "Zoom :", zoomField = new javax.swing.JTextField(10));
		
		System.out.println(this.getPreferredSize());
		System.out.println(nameField.getSize());
		System.out.println();
		
		
		
		setMaximumSize(new java.awt.Dimension(getMaximumSize().width, getPreferredSize().height));
	}

	// --------------------------------------------------
	// Accesseur(s)
	// --------------------------------------------------

	// --------------------------------------------------
	// Mutateur(s)
	// --------------------------------------------------

	public void update(Perspective p) {
		nameField.setText(p.getImage());
		zoomField.setText(Double.toString(p.getZoom()));
	}
	
	public void foo() {
		nameField.setText("Lorem ipsum, dolor sit amet");
	}

	// --------------------------------------------------
	// Méthode(s)
	// --------------------------------------------------

	private void addProperty(int x, int y, String labelText, javax.swing.JTextField field) {
		javax.swing.JLabel label = new javax.swing.JLabel(labelText, javax.swing.JLabel.TRAILING);
		label.setLabelFor(field);
		label.setBackground(java.awt.Color.GREEN);
		field.setEditable(false);
		// field.setBorder(javax.swing.BorderFactory.createEmptyBorder());

		final int MARGIN = 1;
		java.awt.GridBagConstraints labelConstraint = new java.awt.GridBagConstraints();
		labelConstraint.insets = new java.awt.Insets(MARGIN, MARGIN, MARGIN, MARGIN);
		labelConstraint.anchor = java.awt.GridBagConstraints.LINE_END;
		labelConstraint.gridx = x;
		labelConstraint.gridx = y;

		java.awt.GridBagConstraints fieldConstraint = new java.awt.GridBagConstraints();
		fieldConstraint.insets = new java.awt.Insets(MARGIN, MARGIN, MARGIN, MARGIN);
		fieldConstraint.anchor = java.awt.GridBagConstraints.LINE_START;
		fieldConstraint.gridx = x;
		fieldConstraint.gridx = y + 1;

		add(label, labelConstraint);
		add(field, fieldConstraint);
	}

	// --------------------------------------------------
	// Attribut(s)
	// --------------------------------------------------

	private javax.swing.JTextField nameField;
	private javax.swing.JTextField zoomField;
}
