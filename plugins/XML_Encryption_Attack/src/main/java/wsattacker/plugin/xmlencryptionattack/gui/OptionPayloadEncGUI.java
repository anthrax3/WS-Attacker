/**
 * WS-Attacker - A Modular Web Services Penetration Testing Framework Copyright
 * (C) 2013 Dennis Kupser
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */

package wsattacker.plugin.xmlencryptionattack.gui;

import java.util.List;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListModel;
import wsattacker.gui.component.pluginconfiguration.composition.OptionGUI;
import wsattacker.library.xmlencryptionattack.attackengine.AttackConfig;
import wsattacker.library.xmlencryptionattack.util.SimStringStrategyFactory.SimStringStrategy;
import wsattacker.library.xmlencryptionattack.attackengine.oracle.concrete.pkcs1.strategy.PKCS1StrategyFactory.PKCS1Strategy;
import wsattacker.library.xmlencryptionattack.encryptedelements.AbstractEncryptionElement;
import wsattacker.library.xmlencryptionattack.encryptedelements.AbstractRefElement;
import wsattacker.library.xmlencryptionattack.encryptedelements.ElementAttackProperties;
import wsattacker.library.xmlencryptionattack.encryptedelements.data.EncryptedDataElement;
import wsattacker.library.xmlencryptionattack.encryptedelements.key.DataReferenceElement;
import wsattacker.library.xmlencryptionattack.encryptedelements.key.EncryptedKeyElement;
import wsattacker.library.xmlencryptionattack.util.XMLEncryptionConstants.OracleMode;
import wsattacker.library.xmlencryptionattack.util.XMLEncryptionConstants.WrappingAttackMode;
import static wsattacker.library.xmlencryptionattack.util.XMLEncryptionConstants.WrappingAttackMode.ENCRYPTION;
import static wsattacker.library.xmlencryptionattack.util.XMLEncryptionConstants.WrappingAttackMode.SIG_ENC_WRAP;
import wsattacker.library.xmlencryptionattack.util.XMLEncryptionConstants.XMLEncryptionAttackMode;
import static wsattacker.library.xmlutilities.dom.DomUtilities.domToString;
import wsattacker.main.composition.plugin.AbstractPlugin;
import wsattacker.main.composition.plugin.option.AbstractOption;
import wsattacker.plugin.xmlencryptionattack.XMLEncryptionAttack;
import wsattacker.plugin.xmlencryptionattack.option.OptionPayloadEncryption;
import static wsattacker.plugin.xmlencryptionattack.option.OptionPayloadEncryption.NO_ENCDATA_ELEMENTS;
import static wsattacker.plugin.xmlencryptionattack.option.OptionPayloadEncryption.NO_ENCKEY_ELEMENTS;

/**
 * @author Dennis
 */
public class OptionPayloadEncGUI
    extends OptionGUI
{

    private OptionPayloadEncryption m_Option = null;

    private AbstractPlugin m_Plugin;

    private final AbstractEncryptionElement NO_CHOSEN_PAY = null;

    public static final String PROP_OPTION = "option";

    private int m_CurrIndexOfEncKeyEncDataElement = 0;

    /**
     * Get the value of m_Option
     *
     * @return the value of m_Option
     */
    public OptionPayloadEncryption getOption()
    {
        return m_Option;
    }

    /**
     * Set the value of m_Option
     *
     * @param option new value of m_Option
     */
    public void setOption( OptionPayloadEncryption option )
    {

        OptionPayloadEncryption oldOption = this.m_Option;
        this.m_Option = option;
        setEncryptedElementText();
        firePropertyChange( PROP_OPTION, oldOption, option );
    }

    private void initEncKeyFormElements( boolean state )
    {
        initAddDataWrap( state );
        cbEncKeyAttack.setEnabled( state );
        cboxPKSC1Strategy.setEnabled( state );
    }

    private void initAddDataWrap( boolean state )
    {
        if ( state )
        {
            WrappingAttackMode wrapMode = (WrappingAttackMode) cboxWrappingAttack.getSelectedItem();
            if ( ENCRYPTION == wrapMode || SIG_ENC_WRAP == wrapMode )
            {
                cbEncKeyAddWrapp.setEnabled( !cbEncKeyIsSigned.isSelected() );
                cbEncDataAddWrapp.setEnabled( !cbEncDataIsSigned.isSelected() );
            }
            else
            {
                cbEncKeyAddWrapp.setEnabled( false );
                cbEncDataAddWrapp.setEnabled( false );
            }
        }
        else
        {
            cbEncKeyAddWrapp.setEnabled( state );
            cbEncDataAddWrapp.setEnabled( state );
        }
    }

    private void initEncDataFormElements( boolean state )
    {
        cbEncDataAttack.setEnabled( state );
    }

    private void setEncryptedElementText()
    {
        if ( 0 != listEncryptedEl.getModel().getSize() )
        {
            initEncKeyEncDataTextFields();
        }
        else
        // no encrypted elements
        {
            initEncKeyFormElements( false );
            initEncDataFormElements( false );
            cbEncDataIsSigned.setSelected( false );
            cbEncKeyIsSigned.setSelected( false );
            tbEncryptedKey.setText( NO_ENCKEY_ELEMENTS );
            tbEncryptedData.setText( NO_ENCDATA_ELEMENTS );
            tbEncDataCount.setText( "0/0" );
        }

        cbEncKeyAddWrapp.setSelected( false );
        cbEncDataAddWrapp.setSelected( false );
        cbEncKeyAttack.setSelected( false );
        cbEncDataAttack.setSelected( false );
    }

    public void initEncKeyEncDataTextFields()
    {
        AbstractEncryptionElement selectVal = getSelectedListElement();
        ElementAttackProperties attackPropsSelectVal = selectVal.getAttackProperties();
        if ( selectVal instanceof EncryptedKeyElement )
        {
            tbEncryptedKey.setText( domToString( selectVal.getEncryptedElement() ) );
            cbEncKeyIsSigned.setSelected( attackPropsSelectVal.isSigned() );
            DataReferenceElement encRef =
                (DataReferenceElement) ( (EncryptedKeyElement) selectVal ).getReferenceElementList().get( m_CurrIndexOfEncKeyEncDataElement );
            ElementAttackProperties attackPropsEncRef = encRef.getRefEncData().getAttackProperties();
            tbEncDataCount.setText( ( m_CurrIndexOfEncKeyEncDataElement + 1 ) + "/"
                + ( (EncryptedKeyElement) selectVal ).getReferenceElementList().size() );
            cbEncDataIsSigned.setSelected( attackPropsEncRef.isSigned() );
            tbEncryptedData.setText( domToString( encRef.getRefEncData().getEncryptedElement() ) );
            initEncKeyFormElements( true );
            initEncDataFormElements( true );
        }
        else
        {
            tbEncryptedKey.setText( NO_ENCKEY_ELEMENTS );
            initEncKeyFormElements( false );
            initEncDataFormElements( true );

            if ( null != selectVal.getKeyInfoElement() ) // case enckey inside encdata
            {
                if ( null != selectVal.getKeyInfoElement().getEncryptedKeyElement() )
                    cboxPKSC1Strategy.setEnabled( true );
            }

            tbEncryptedData.setText( domToString( selectVal.getEncryptedElement() ) );
            cbEncDataIsSigned.setSelected( attackPropsSelectVal.isSigned() );
            cbEncKeyIsSigned.setSelected( false );
            tbEncDataCount.setText( "1/1" );
        }
    }

    private AbstractEncryptionElement getSelectedListElement()
    {
        int selectIdx = listEncryptedEl.getSelectedIndex();
        AbstractEncryptionElement selectVal = null;
        if ( 0 <= selectIdx )
            selectVal = (AbstractEncryptionElement) listEncryptedEl.getModel().getElementAt( selectIdx );
        return selectVal;
    }

    /**
     * Creates new form OptionVarcharGUI
     */
    public OptionPayloadEncGUI()
    {
        initComponents();
    }

    public OptionPayloadEncGUI( OptionPayloadEncryption option, AbstractPlugin plug )
    {
        this.m_Option = option;
        this.m_Plugin = (XMLEncryptionAttack) plug;
        initComponents();
        setEncryptedElementText();
        if ( null != m_Option.getTimestamp() )
        {
            cbIsTimeStamp.setSelected( true );
            if ( m_Option.getTimestamp().isSigned() )
                cbIsTimeStamp.setText( "Timestamp Signed" );
            else
                cbIsTimeStamp.setText( "Timestamp" );
        }
        else
            cbIsTimeStamp.setSelected( false );
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings( "unchecked" )
        // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
        private void initComponents() {

                lbEncKeyPay = new javax.swing.JLabel();
                lbEncryptedData = new javax.swing.JLabel();
                cbEncKeyIsSigned = new javax.swing.JCheckBox();
                cbEncDataIsSigned = new javax.swing.JCheckBox();
                cbEncKeyAttack = new javax.swing.JCheckBox();
                cbEncDataAttack = new javax.swing.JCheckBox();
                tbEncDataCount = new javax.swing.JTextField();
                cboxAttack = new javax.swing.JComboBox();
                cboxOracle = new javax.swing.JComboBox();
                lbAttackCombo = new javax.swing.JLabel();
                lbOracleCombo = new javax.swing.JLabel();
                lbWrappingAttack = new javax.swing.JLabel();
                cboxWrappingAttack = new javax.swing.JComboBox();
                lbStringComp = new javax.swing.JLabel();
                cboxStringComp = new javax.swing.JComboBox();
                jPanelEncKey = new javax.swing.JPanel();
                jScrollPane1 = new org.fife.ui.rtextarea.RTextScrollPane();
                tbEncryptedKey = new org.fife.ui.rsyntaxtextarea.RSyntaxTextArea();
                tbEncryptedKey.setLineWrap(true);
                tbEncryptedKey.setWrapStyleWord(false);
                jPanelEncData = new javax.swing.JPanel();
                jScrollPane3 = new org.fife.ui.rtextarea.RTextScrollPane();
                tbEncryptedData = new org.fife.ui.rsyntaxtextarea.RSyntaxTextArea();
                tbEncryptedData.setLineWrap(true);
                tbEncryptedData.setWrapStyleWord(false);
                jScrollPane2 = new javax.swing.JScrollPane();
                final ListModel<AbstractEncryptionElement> listModel = new PayloadListModel(m_Option.getPayloads());
                listEncryptedEl = new JList<AbstractEncryptionElement>(listModel);
                lbTitleEncElements = new javax.swing.JLabel();
                btnPrevEncData = new javax.swing.JButton();
                btnNextEncData = new javax.swing.JButton();
                lbStringCmpTresh = new javax.swing.JLabel();
                tbCmpThres = new javax.swing.JTextField();
                lbEncElements = new javax.swing.JLabel();
                cbIsTimeStamp = new javax.swing.JCheckBox();
                tbThresHoldWrap = new javax.swing.JTextField();
                lbThresWrap = new javax.swing.JLabel();
                cboxPKSC1Strategy = new javax.swing.JComboBox();
                cbEncKeyAddWrapp = new javax.swing.JCheckBox();
                lbPKCS1Strategy = new javax.swing.JLabel();
                cbEncDataAddWrapp = new javax.swing.JCheckBox();
                cbEncTypeWeak = new javax.swing.JCheckBox();
                jLabel1 = new javax.swing.JLabel();

                lbEncKeyPay.setText("EncryptedKey:");

                lbEncryptedData.setText("EncryptedData:");

                cbEncKeyIsSigned.setText("isSigned");
                cbEncKeyIsSigned.setToolTipText("");
                cbEncKeyIsSigned.setEnabled(false);

                cbEncDataIsSigned.setText("isSigned");
                cbEncDataIsSigned.setEnabled(false);

                cbEncKeyAttack.setText("isAttackPayload");
                cbEncKeyAttack.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                cbEncKeyAttackActionPerformed(evt);
                        }
                });

                cbEncDataAttack.setText("isAttackPayload");
                cbEncDataAttack.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                cbEncDataAttackActionPerformed(evt);
                        }
                });

                tbEncDataCount.setHorizontalAlignment(javax.swing.JTextField.CENTER);
                tbEncDataCount.setEnabled(false);

                cboxAttack.setModel(new javax.swing.DefaultComboBoxModel(XMLEncryptionAttackMode.values()));
                cboxAttack.addItemListener(new java.awt.event.ItemListener() {
                        public void itemStateChanged(java.awt.event.ItemEvent evt) {
                                cboxAttackItemStateChanged(evt);
                        }
                });

                cboxOracle.setModel(new javax.swing.DefaultComboBoxModel(OracleMode.values()));
                cboxOracle.addItemListener(new java.awt.event.ItemListener() {
                        public void itemStateChanged(java.awt.event.ItemEvent evt) {
                                cboxOracleItemStateChanged(evt);
                        }
                });

                lbAttackCombo.setText("Attack:");

                lbOracleCombo.setText("Oracle Type:");

                lbWrappingAttack.setText("Wrapping Attack:");

                cboxWrappingAttack.setModel(new javax.swing.DefaultComboBoxModel(WrappingAttackMode.values()));
                cboxWrappingAttack.addItemListener(new java.awt.event.ItemListener() {
                        public void itemStateChanged(java.awt.event.ItemEvent evt) {
                                cboxWrappingAttackItemStateChanged(evt);
                        }
                });

                lbStringComp.setText("StringCompare:");

                cboxStringComp.setModel(new javax.swing.DefaultComboBoxModel(SimStringStrategy.values()));
                cboxStringComp.addItemListener(new java.awt.event.ItemListener() {
                        public void itemStateChanged(java.awt.event.ItemEvent evt) {
                                cboxStringCompItemStateChanged(evt);
                        }
                });

                tbEncryptedKey.setEditable(false);
                tbEncryptedKey.setToolTipText("");
                tbEncryptedKey.setMaximumSize(new java.awt.Dimension(700, 133));
                tbEncryptedKey.setMinimumSize(new java.awt.Dimension(700, 133));
                tbEncryptedKey.setName(""); // NOI18N
                tbEncryptedKey.setPreferredSize(new java.awt.Dimension(700, 133));
                tbEncryptedKey.setSyntaxEditingStyle("text/xml");
                jScrollPane1.setViewportView(tbEncryptedKey);

                javax.swing.GroupLayout jPanelEncKeyLayout = new javax.swing.GroupLayout(jPanelEncKey);
                jPanelEncKey.setLayout(jPanelEncKeyLayout);
                jPanelEncKeyLayout.setHorizontalGroup(
                        jPanelEncKeyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanelEncKeyLayout.createSequentialGroup()
                                .addGap(0, 0, 0)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                .addGap(0, 0, 0))
                );
                jPanelEncKeyLayout.setVerticalGroup(
                        jPanelEncKeyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanelEncKeyLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE)
                                .addContainerGap())
                );

                tbEncryptedData.setEditable(false);
                tbEncryptedData.setMaximumSize(new java.awt.Dimension(701, 200));
                tbEncryptedData.setMinimumSize(new java.awt.Dimension(701, 200));
                tbEncryptedData.setName(""); // NOI18N
                tbEncryptedData.setPreferredSize(new java.awt.Dimension(701, 200));
                tbEncryptedData.setSyntaxEditingStyle("text/xml");
                jScrollPane3.setViewportView(tbEncryptedData);

                javax.swing.GroupLayout jPanelEncDataLayout = new javax.swing.GroupLayout(jPanelEncData);
                jPanelEncData.setLayout(jPanelEncDataLayout);
                jPanelEncDataLayout.setHorizontalGroup(
                        jPanelEncDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanelEncDataLayout.createSequentialGroup()
                                .addGap(0, 0, 0)
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                );
                jPanelEncDataLayout.setVerticalGroup(
                        jPanelEncDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanelEncDataLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE)
                                .addContainerGap())
                );

                listEncryptedEl.setCellRenderer(new PayloadListCellRenderer());
                listEncryptedEl.setSelectedIndex(0);
                listEncryptedEl.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
                listEncryptedEl.setToolTipText("");
                listEncryptedEl.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
                        public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                                listEncryptedElValueChanged(evt);
                        }
                });
                jScrollPane2.setViewportView(listEncryptedEl);

                lbTitleEncElements.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
                lbTitleEncElements.setText("Detected Encrypted Elements:");

                btnPrevEncData.setFont(new java.awt.Font("Tahoma", 0, 8)); // NOI18N
                btnPrevEncData.setText("<");
                btnPrevEncData.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
                btnPrevEncData.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
                btnPrevEncData.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
                btnPrevEncData.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                btnPrevEncDataActionPerformed(evt);
                        }
                });

                btnNextEncData.setFont(new java.awt.Font("Tahoma", 0, 8)); // NOI18N
                btnNextEncData.setText(">");
                btnNextEncData.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
                btnNextEncData.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
                btnNextEncData.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
                btnNextEncData.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                btnNextEncDataBtnActionPerformed(evt);
                        }
                });

                lbStringCmpTresh.setText("Threshold General Error:");

                tbCmpThres.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
                tbCmpThres.setText(Double.toString(AttackConfig.DEFAULT_STRING_CMP_WRAP_ERROR_THRESHOLD)
                );
                tbCmpThres.setToolTipText("");
                tbCmpThres.addFocusListener(new java.awt.event.FocusAdapter() {
                        public void focusLost(java.awt.event.FocusEvent evt) {
                                tbCmpThresFocusLost(evt);
                        }
                });

                lbEncElements.setText("Elements:");

                cbIsTimeStamp.setText("TimeStamp");
                cbIsTimeStamp.setEnabled(false);
                cbIsTimeStamp.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                cbIsTimeStampActionPerformed(evt);
                        }
                });

                tbThresHoldWrap.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
                tbThresHoldWrap.setText(Double.toString(AttackConfig.DEFAULT_STRING_CMP_THRESHOLD));
                tbThresHoldWrap.addFocusListener(new java.awt.event.FocusAdapter() {
                        public void focusLost(java.awt.event.FocusEvent evt) {
                                tbThresHoldWrapFocusLost(evt);
                        }
                });

                lbThresWrap.setText("Threshold Wrap Error:");

                cboxPKSC1Strategy.setModel(new javax.swing.DefaultComboBoxModel(PKCS1Strategy.values()));
                cboxPKSC1Strategy.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                cboxPKSC1StrategyActionPerformed(evt);
                        }
                });

                cbEncKeyAddWrapp.setText("isAddWrap");
                cbEncKeyAddWrapp.setEnabled(false);
                cbEncKeyAddWrapp.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                cbEncKeyAddWrappActionPerformed(evt);
                        }
                });

                lbPKCS1Strategy.setText("PKCS1 Strategy");

                cbEncDataAddWrapp.setText("isAddWrapp");
                cbEncDataAddWrapp.setEnabled(false);
                cbEncDataAddWrapp.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                cbEncDataAddWrappActionPerformed(evt);
                        }
                });

                cbEncTypeWeak.setText("useTypeWeakness");
                cbEncTypeWeak.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                cbEncTypeWeakActionPerformed(evt);
                        }
                });

                jLabel1.setText("Configuration:");

                javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
                this.setLayout(layout);
                layout.setHorizontalGroup(
                        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(lbTitleEncElements)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addGap(1, 1, 1)
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addComponent(lbEncElements, javax.swing.GroupLayout.Alignment.TRAILING)
                                                                        .addComponent(lbEncKeyPay, javax.swing.GroupLayout.Alignment.TRAILING)))
                                                        .addComponent(cbEncKeyAttack)
                                                        .addComponent(cbEncKeyIsSigned)
                                                        .addComponent(cbEncKeyAddWrapp)
                                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                                .addComponent(cboxPKSC1Strategy, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                .addComponent(lbPKCS1Strategy, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                                        .addComponent(jLabel1)
                                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                                .addComponent(tbEncDataCount)
                                                                .addGroup(layout.createSequentialGroup()
                                                                        .addComponent(btnPrevEncData)
                                                                        .addGap(2, 2, 2)
                                                                        .addComponent(btnNextEncData)))
                                                        .addComponent(lbEncryptedData)
                                                        .addComponent(cbEncDataAttack)
                                                        .addComponent(cbEncDataIsSigned)
                                                        .addComponent(cbEncDataAddWrapp)
                                                        .addComponent(cbEncTypeWeak))
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                                .addGap(12, 12, 12)
                                                                .addComponent(jPanelEncKey, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addGap(9, 9, 9)
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addComponent(jPanelEncData, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                        .addGroup(layout.createSequentialGroup()
                                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                                                        .addComponent(lbThresWrap, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                                        .addComponent(lbAttackCombo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                                        .addComponent(lbWrappingAttack, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                                                        .addComponent(cboxWrappingAttack, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                                        .addComponent(tbThresHoldWrap, javax.swing.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
                                                                                        .addComponent(cboxAttack, 0, 140, Short.MAX_VALUE)
                                                                                        .addComponent(cbIsTimeStamp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                                        .addGroup(layout.createSequentialGroup()
                                                                                                .addComponent(lbStringComp)
                                                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                                .addComponent(cboxStringComp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                                        .addGroup(layout.createSequentialGroup()
                                                                                                .addComponent(lbOracleCombo)
                                                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                                .addComponent(cboxOracle, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                                        .addGroup(layout.createSequentialGroup()
                                                                                                .addComponent(lbStringCmpTresh)
                                                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                                .addComponent(tbCmpThres, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)))))))))
                                .addGap(9, 9, 9))
                );

                layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {lbEncElements, lbEncKeyPay, lbEncryptedData});

                layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {lbAttackCombo, lbThresWrap, lbWrappingAttack});

                layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {lbOracleCombo, lbStringCmpTresh, lbStringComp});

                layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cboxAttack, cboxWrappingAttack, tbThresHoldWrap});

                layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cboxOracle, cboxStringComp, tbCmpThres});

                layout.setVerticalGroup(
                        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(lbTitleEncElements)
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(lbEncElements)
                                                        .addComponent(cbIsTimeStamp))
                                                .addGap(48, 48, 48)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel1)
                                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                .addComponent(lbAttackCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addComponent(cboxAttack, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addComponent(lbOracleCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addComponent(cboxOracle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                        .addComponent(lbStringComp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(cboxStringComp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                        .addComponent(lbStringCmpTresh)
                                                                        .addComponent(tbCmpThres, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                        .addComponent(lbWrappingAttack, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(cboxWrappingAttack, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                        .addComponent(lbThresWrap)
                                                                        .addComponent(tbThresHoldWrap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lbEncKeyPay)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(cbEncKeyAttack)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(cbEncKeyIsSigned)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(cbEncKeyAddWrapp)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(lbPKCS1Strategy)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(cboxPKSC1Strategy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(jPanelEncKey, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(13, 13, 13)
                                .addComponent(lbEncryptedData)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(tbEncDataCount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                        .addComponent(btnNextEncData)
                                                        .addComponent(btnPrevEncData, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(cbEncDataAttack)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(cbEncDataIsSigned, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(cbEncDataAddWrapp)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(cbEncTypeWeak))
                                        .addComponent(jPanelEncData, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(14, 14, 14))
                );
        }// </editor-fold>//GEN-END:initComponents

    private void tbCmpThresFocusLost( java.awt.event.FocusEvent evt )
    {// GEN-FIRST:event_tbCmpThresFocusLost
        double dValue = 1.0;
        try
        {
            dValue = Double.parseDouble( tbCmpThres.getText() );
            if ( 0.0 > dValue || 1.0 < dValue )
            {
                JOptionPane.showMessageDialog( null, "Only values from 0.0-1.0 allowed.", "Invalid Threshold value",
                                               JOptionPane.OK_OPTION );
                dValue = 1.0;
            }
        }
        catch ( Exception e )
        {
            JOptionPane.showMessageDialog( null, "Only values from 0.0-1.0 allowed.", "Invalid Threshold value",
                                           JOptionPane.OK_OPTION );
            dValue = 1.0;
        }
        tbCmpThres.setText( "" + dValue );
        AttackConfig attackConfig = ( (XMLEncryptionAttack) m_Plugin ).getAttackCfg();
        attackConfig.setStringCmpThresHold( dValue );
    }// GEN-LAST:event_tbCmpThresFocusLost

    private void tbThresHoldWrapFocusLost( java.awt.event.FocusEvent evt )
    {// GEN-FIRST:event_tbThresHoldWrapFocusLost
        double dValue = 1.0;
        try
        {
            dValue = Double.parseDouble( tbThresHoldWrap.getText() );
            if ( 0.0 > dValue || 1.0 < dValue )
            {
                JOptionPane.showMessageDialog( null, "Only values from 0.0-1.0 allowed.", "Invalid Threshold value",
                                               JOptionPane.OK_OPTION );
                dValue = 1.0;
            }
        }
        catch ( Exception e )
        {
            JOptionPane.showMessageDialog( null, "Only values from 0.0-1.0 allowed.", "Invalid Threshold value",
                                           JOptionPane.OK_OPTION );
            dValue = 1.0;
        }
        tbThresHoldWrap.setText( "" + dValue );
        AttackConfig attackConfig = ( (XMLEncryptionAttack) m_Plugin ).getAttackCfg();
        attackConfig.setStringCmpWrappThreshold( dValue );
    }// GEN-LAST:event_tbThresHoldWrapFocusLost

    private void cbIsTimeStampActionPerformed( java.awt.event.ActionEvent evt )
    {// GEN-FIRST:event_cbIsTimeStampActionPerformed
     // TODO add your handling code here:
    }// GEN-LAST:event_cbIsTimeStampActionPerformed

    private void cbEncTypeWeakActionPerformed( java.awt.event.ActionEvent evt )
    {// GEN-FIRST:event_cbEncTypeWeakActionPerformed
        if ( null != m_Option && null != m_Plugin )
        {
            AttackConfig attackConfig = ( (XMLEncryptionAttack) m_Plugin ).getAttackCfg();
            attackConfig.setIsEncTypeWeakness( cbEncTypeWeak.isSelected() );
        }
    }// GEN-LAST:event_cbEncTypeWeakActionPerformed

    private void cbEncDataAddWrappActionPerformed( java.awt.event.ActionEvent evt )
    {// GEN-FIRST:event_EncDataAddWrappActionPerformed
        if ( null != m_Option && null != m_Plugin )
        {
            checkIsAddDataWrap();
        }
    }// GEN-LAST:event_EncDataAddWrappActionPerformed

    private void cboxAttackItemStateChanged( java.awt.event.ItemEvent evt )
    {// GEN-FIRST:event_AttackComboItemStateChanged
        if ( m_Plugin != null && cboxAttack.getSelectedIndex() >= 0 )
        {
            AttackConfig attackConfig = ( (XMLEncryptionAttack) m_Plugin ).getAttackCfg();
            attackConfig.setXMLEncryptionAttack( (XMLEncryptionAttackMode) cboxAttack.getSelectedItem() );
            setChosenPayloads( NO_CHOSEN_PAY, NO_CHOSEN_PAY );
            cbEncKeyAttack.setSelected( false );
            cbEncDataAttack.setSelected( false );
            ( (XMLEncryptionAttack) m_Plugin ).checkState();
        }
    }// GEN-LAST:event_AttackComboItemStateChanged

    private void setChosenPayloads( AbstractEncryptionElement attackPay, AbstractEncryptionElement wrapPay )
    {
        AttackConfig attackConfig = ( (XMLEncryptionAttack) m_Plugin ).getAttackCfg();

        initAddWrapProperties( attackConfig );

        attackConfig.setChosenAttackPayload( attackPay );
        attackConfig.setChosenWrapPayload( wrapPay );
        if ( null != attackPay )
            attackPay.getAttackProperties().setIsAdditionalWrap( false );
        if ( null != wrapPay )
            wrapPay.getAttackProperties().setIsAdditionalWrap( false );

        if ( null != wrapPay )
            m_Option.setSigWrappPayload( wrapPay );
        checkIsAddDataWrap();
    }

    private void initAddWrapProperties( AttackConfig attackConfig )
    {
        if ( null != attackConfig.getChosenWrapPayload() )
        {
            if ( !attackConfig.getChosenWrapPayload().getAttackProperties().isSigned() )
            {
                m_Option.setIsAddWrap( false, attackConfig.getChosenWrapPayload() );
                cbEncKeyAddWrapp.setSelected( false );
                cbEncDataAddWrapp.setSelected( false );
            }

            if ( !attackConfig.getChosenAttackPayload().getAttackProperties().isSigned() )
            {
                m_Option.setIsAddWrap( false, attackConfig.getChosenAttackPayload() );
                cbEncKeyAddWrapp.setSelected( false );
                cbEncDataAddWrapp.setSelected( false );
            }
        }
    }

    private void cboxOracleItemStateChanged( java.awt.event.ItemEvent evt )
    {// GEN-FIRST:event_OracleComboItemStateChanged
        if ( m_Plugin != null && cboxOracle.getSelectedIndex() >= 0 )
        {
            AttackConfig attackConfig = ( (XMLEncryptionAttack) m_Plugin ).getAttackCfg();
            attackConfig.setOracleMode( (OracleMode) cboxOracle.getSelectedItem() );
        }
    }// GEN-LAST:event_OracleComboItemStateChanged

    private void cboxWrappingAttackItemStateChanged( java.awt.event.ItemEvent evt )
    {// GEN-FIRST:event_WrappingAttackComboItemStateChanged
        if ( m_Plugin != null && cboxWrappingAttack.getSelectedIndex() >= 0 )
        {
            WrappingAttackMode wrapMode = (WrappingAttackMode) cboxWrappingAttack.getSelectedItem();
            if ( ENCRYPTION == wrapMode )
            {
                setAdditionalWrapEnabledState( true );
            }
            else
            {
                setAdditionalWrapEnabledState( false );
            }
            setAdditionalWrapSelectState( false );

            AttackConfig attackConfig = ( (XMLEncryptionAttack) m_Plugin ).getAttackCfg();
            attackConfig.setWrappingMode( (WrappingAttackMode) cboxWrappingAttack.getSelectedItem() );
            checkIsAddDataWrap();
        }
    }// GEN-LAST:event_WrappingAttackComboItemStateChanged

    private void setAdditionalWrapEnabledState( boolean state )
    {
        cbEncDataAddWrapp.setEnabled( state );
        cbEncKeyAddWrapp.setEnabled( state );
    }

    private void setAdditionalWrapSelectState( boolean state )
    {
        cbEncDataAddWrapp.setSelected( state );
        cbEncKeyAddWrapp.setSelected( state );
    }

    private void cbEncKeyAttackActionPerformed( java.awt.event.ActionEvent evt )
    {// GEN-FIRST:event_IsEncKeyAttackActionPerformed

        if ( cbEncKeyAttack.isSelected() && null != m_Option && null != m_Plugin )
        {
            AbstractEncryptionElement selctEl = getSelectedListElement();
            if ( selctEl instanceof EncryptedKeyElement )
            {
                List<AbstractRefElement> encRefs = ( (EncryptedKeyElement) selctEl ).getReferenceElementList();
                setChosenPayloads( selctEl, selctEl );
                ( (EncryptedKeyElement) selctEl ).setWrappingEncDataIndex( m_CurrIndexOfEncKeyEncDataElement );
            }
            else
            {
                setChosenPayloads( NO_CHOSEN_PAY, NO_CHOSEN_PAY );
                cbEncKeyAttack.setSelected( false );
            }
            cbEncDataAttack.setSelected( false );
        }
        else if ( null != m_Plugin )
        {
            setChosenPayloads( NO_CHOSEN_PAY, NO_CHOSEN_PAY );
        }

        ( (XMLEncryptionAttack) m_Plugin ).checkState();
    }// GEN-LAST:event_IsEncKeyAttackActionPerformed

    private void cbEncDataAttackActionPerformed( java.awt.event.ActionEvent evt )
    {// GEN-FIRST:event_IsEncDataAttackActionPerformed
        if ( cbEncDataAttack.isSelected() && null != m_Option && null != m_Plugin )
        {
            // EncData Only
            if ( tbEncryptedKey.getText().equals( NO_ENCKEY_ELEMENTS ) )
            {
                if ( getSelectedListElement() instanceof EncryptedDataElement )
                {
                    setChosenPayloads( getSelectedListElement(), getSelectedListElement() );
                }
                else
                {
                    setChosenPayloads( NO_CHOSEN_PAY, NO_CHOSEN_PAY );
                    cbEncDataAttack.setSelected( false );
                }
            }
            else if ( getSelectedListElement() instanceof EncryptedKeyElement ) // encData of an encKey element
            {
                EncryptedKeyElement encKey = (EncryptedKeyElement) getSelectedListElement();
                List<AbstractRefElement> dataRef = encKey.getReferenceElementList();

                if ( dataRef.size() > m_CurrIndexOfEncKeyEncDataElement )
                {
                    encKey.setWrappingEncDataIndex( m_CurrIndexOfEncKeyEncDataElement );
                    AbstractEncryptionElement refPay =
                        ( (DataReferenceElement) dataRef.get( m_CurrIndexOfEncKeyEncDataElement ) ).getRefEncData();
                    setChosenPayloads( refPay, encKey );
                }
                else
                    throw new IllegalArgumentException( "curr data index smaller than key reference list size" );
            }

            cbEncKeyAttack.setSelected( false );

        }
        else if ( null != m_Plugin )
        {
            setChosenPayloads( NO_CHOSEN_PAY, NO_CHOSEN_PAY );
        }
        ( (XMLEncryptionAttack) m_Plugin ).checkState();

    }// GEN-LAST:event_IsEncDataAttackActionPerformed

    private void cboxStringCompItemStateChanged( java.awt.event.ItemEvent evt )
    {// GEN-FIRST:event_StringCompComboItemStateChanged
        if ( m_Plugin != null && cboxStringComp.getSelectedIndex() >= 0 )
        {
            AttackConfig attackConfig = ( (XMLEncryptionAttack) m_Plugin ).getAttackCfg();
            attackConfig.setSimStringStrategyType( (SimStringStrategy) cboxStringComp.getSelectedItem() );
        }
    }// GEN-LAST:event_StringCompComboItemStateChanged

    private void listEncryptedElValueChanged( javax.swing.event.ListSelectionEvent evt )
    {// GEN-FIRST:event_EncryptedElListValueChanged
        setEncryptedElementText();
    }// GEN-LAST:event_EncryptedElListValueChanged

    private void btnPrevEncDataActionPerformed( java.awt.event.ActionEvent evt )
    {// GEN-FIRST:event_PrevEncDataBtnActionPerformed
        if ( getSelectedListElement() instanceof EncryptedKeyElement && null != m_Option && null != m_Plugin )
        {
            EncryptedKeyElement encKey = (EncryptedKeyElement) getSelectedListElement();
            List<AbstractRefElement> dataRef = encKey.getReferenceElementList();
            m_CurrIndexOfEncKeyEncDataElement--;
            if ( 0 > m_CurrIndexOfEncKeyEncDataElement )
                m_CurrIndexOfEncKeyEncDataElement = 0;

            if ( dataRef.size() > m_CurrIndexOfEncKeyEncDataElement && 0 <= m_CurrIndexOfEncKeyEncDataElement )
            {
                setEncryptedElementText();
            }
            else
                throw new IllegalArgumentException( "enckey without encData" );
        }
    }// GEN-LAST:event_PrevEncDataBtnActionPerformed

    private void btnNextEncDataBtnActionPerformed( java.awt.event.ActionEvent evt )
    {// GEN-FIRST:event_NextEncDataBtnBtnActionPerformed
        if ( getSelectedListElement() instanceof EncryptedKeyElement && null != m_Option && null != m_Plugin )
        {
            EncryptedKeyElement encKey = (EncryptedKeyElement) getSelectedListElement();
            List<AbstractRefElement> dataRef = encKey.getReferenceElementList();
            m_CurrIndexOfEncKeyEncDataElement++;
            if ( dataRef.size() <= m_CurrIndexOfEncKeyEncDataElement )
                m_CurrIndexOfEncKeyEncDataElement = 0;

            if ( dataRef.size() > m_CurrIndexOfEncKeyEncDataElement && 0 <= m_CurrIndexOfEncKeyEncDataElement )
            {
                setEncryptedElementText();
            }
            else
                throw new IllegalArgumentException( "enckey without encData" );
        }
    }// GEN-LAST:event_NextEncDataBtnBtnActionPerformed

    private void cboxPKSC1StrategyActionPerformed( java.awt.event.ActionEvent evt )
    {// GEN-FIRST:event_PKSC1StrategyCmbBoxActionPerformed
        if ( m_Plugin != null && cboxPKSC1Strategy.getSelectedIndex() >= 0 )
        {
            AttackConfig attackConfig = ( (XMLEncryptionAttack) m_Plugin ).getAttackCfg();
            attackConfig.getPKCS1AttackCfg().setPKCS1Strategy( (PKCS1Strategy) cboxPKSC1Strategy.getSelectedItem() );
        }
    }// GEN-LAST:event_PKSC1StrategyCmbBoxActionPerformed

    private void cbEncKeyAddWrappActionPerformed( java.awt.event.ActionEvent evt )
    {// GEN-FIRST:event_EncKeyAddWrappActionPerformed
        if ( null != m_Option && null != m_Plugin )
        {
            checkIsAddDataWrap();
        }
    }

    public void checkIsAddDataWrap()
    {
        AttackConfig attackConfig = ( (XMLEncryptionAttack) m_Plugin ).getAttackCfg();
        AbstractEncryptionElement wrapPay = attackConfig.getChosenWrapPayload();
        AbstractEncryptionElement attackPay = attackConfig.getChosenAttackPayload();
        // for cbc attack, too
        if ( null != attackPay && null != wrapPay )
        {
            if ( wrapPay instanceof EncryptedKeyElement || attackPay instanceof EncryptedKeyElement )
            {
                if ( !cbEncKeyIsSigned.isSelected()
                    && cbEncKeyAddWrapp.isEnabled()
                    && ( cbEncDataAddWrapp.isSelected() || attackPay instanceof EncryptedKeyElement || cbEncDataIsSigned.isSelected() ) ) // encKey
                {
                    m_Option.setIsAddWrap( cbEncKeyAddWrapp.isSelected(), wrapPay );
                }
                else
                {
                    cbEncKeyAddWrapp.setSelected( false );
                }
            }

            if ( wrapPay instanceof EncryptedDataElement || attackPay instanceof EncryptedDataElement )
            {
                if ( !cbEncDataIsSigned.isSelected() && cbEncDataAddWrapp.isEnabled() )
                {
                    if ( wrapPay instanceof EncryptedDataElement )
                        m_Option.setIsAddWrap( cbEncDataAddWrapp.isSelected(), wrapPay );
                    else
                        m_Option.setIsAddWrap( cbEncDataAddWrapp.isSelected(), attackPay );
                }
                else
                {
                    cbEncDataAddWrapp.setSelected( false );
                }

            }
            else
            {
                cbEncDataAddWrapp.setSelected( false );
            }

        }
        else
        {
            setAdditionalWrapSelectState( false );
        }

    }// GEN-LAST:event_EncKeyAddWrappActionPerformed

        // Variables declaration - do not modify//GEN-BEGIN:variables
        private javax.swing.JButton btnNextEncData;
        private javax.swing.JButton btnPrevEncData;
        private javax.swing.JCheckBox cbEncDataAddWrapp;
        private javax.swing.JCheckBox cbEncDataAttack;
        private javax.swing.JCheckBox cbEncDataIsSigned;
        private javax.swing.JCheckBox cbEncKeyAddWrapp;
        private javax.swing.JCheckBox cbEncKeyAttack;
        private javax.swing.JCheckBox cbEncKeyIsSigned;
        private javax.swing.JCheckBox cbEncTypeWeak;
        private javax.swing.JCheckBox cbIsTimeStamp;
        private javax.swing.JComboBox cboxAttack;
        private javax.swing.JComboBox cboxOracle;
        private javax.swing.JComboBox cboxPKSC1Strategy;
        private javax.swing.JComboBox cboxStringComp;
        private javax.swing.JComboBox cboxWrappingAttack;
        private javax.swing.JLabel jLabel1;
        private javax.swing.JPanel jPanelEncData;
        private javax.swing.JPanel jPanelEncKey;
        private org.fife.ui.rtextarea.RTextScrollPane jScrollPane1;
        private javax.swing.JScrollPane jScrollPane2;
        private org.fife.ui.rtextarea.RTextScrollPane jScrollPane3;
        private javax.swing.JLabel lbAttackCombo;
        private javax.swing.JLabel lbEncElements;
        private javax.swing.JLabel lbEncKeyPay;
        private javax.swing.JLabel lbEncryptedData;
        private javax.swing.JLabel lbOracleCombo;
        private javax.swing.JLabel lbPKCS1Strategy;
        private javax.swing.JLabel lbStringCmpTresh;
        private javax.swing.JLabel lbStringComp;
        private javax.swing.JLabel lbThresWrap;
        private javax.swing.JLabel lbTitleEncElements;
        private javax.swing.JLabel lbWrappingAttack;
        private javax.swing.JList listEncryptedEl;
        private javax.swing.JTextField tbCmpThres;
        private javax.swing.JTextField tbEncDataCount;
        private org.fife.ui.rsyntaxtextarea.RSyntaxTextArea tbEncryptedData;
        private org.fife.ui.rsyntaxtextarea.RSyntaxTextArea tbEncryptedKey;
        private javax.swing.JTextField tbThresHoldWrap;
        // End of variables declaration//GEN-END:variables

    @Override
    public void bindingDoUnbind()
    {
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose
        // Tools | Templates.
    }

    @Override
    public AbstractOption getUsedOption()
    {
        return m_Option;
    }
}
