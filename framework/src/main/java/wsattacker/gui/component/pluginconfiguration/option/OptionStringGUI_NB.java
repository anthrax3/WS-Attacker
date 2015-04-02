/**
 * WS-Attacker - A Modular Web Services Penetration Testing Framework Copyright
 * (C) 2010 Christian Mainka
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
package wsattacker.gui.component.pluginconfiguration.option;

import org.jdesktop.beansbinding.Validator;
import wsattacker.gui.component.pluginconfiguration.composition.OptionGUI;
import wsattacker.main.composition.plugin.option.AbstractOption;
import wsattacker.main.composition.plugin.option.AbstractOptionString;
import wsattacker.main.plugin.option.OptionSimpleText;

public class OptionStringGUI_NB
    extends OptionGUI
{

    private AbstractOptionString option = new OptionSimpleText( "Sample Option", "Sample Description" );

    public static final String PROP_OPTION = "option";

    /**
     * Get the value of option
     * 
     * @return the value of option
     */
    public AbstractOptionString getOption()
    {
        return option;
    }

    /**
     * Set the value of option
     * 
     * @param option new value of option
     */
    public void setOption( AbstractOptionString option )
    {
        AbstractOptionString oldOption = this.option;
        this.option = option;
        firePropertyChange( PROP_OPTION, oldOption, option );
    }

    /**
     * Creates new form OptionVarcharGUI_NB
     */
    public OptionStringGUI_NB()
    {
        initComponents();
    }

    public OptionStringGUI_NB( AbstractOptionString option )
    {
        this.option = option;
        initComponents();
    }

    private void setValueBackgroundIfError( boolean error )
    {
        if ( error )
        {
            value.setBackground( COLOR_ERROR );
        }
        else
        {
            value.setBackground( COLOR_OK );
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings( "unchecked" )
    // <editor-fold defaultstate="collapsed"
    // desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        name = new javax.swing.JLabel();
        description = new javax.swing.JLabel();
        lengthLabel = new javax.swing.JLabel();
        maxValue = new javax.swing.JLabel();
        valueScrollPane = new javax.swing.JScrollPane();
        value = new javax.swing.JTextPane();
        typeLabel = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        name.setFont( new java.awt.Font( "Dialog", 1, 18 ) ); // NOI18N

        org.jdesktop.beansbinding.Binding binding =
            org.jdesktop.beansbinding.Bindings.createAutoBinding( org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                                                                  this,
                                                                  org.jdesktop.beansbinding.ELProperty.create( "${option.name}" ),
                                                                  name,
                                                                  org.jdesktop.beansbinding.BeanProperty.create( "text" ) );
        binding.setSourceNullValue( "Option Name" );
        binding.setSourceUnreadableValue( "Option Name" );
        bindingGroup.addBinding( binding );

        description.setFont( new java.awt.Font( "Dialog", 0, 12 ) ); // NOI18N

        binding =
            org.jdesktop.beansbinding.Bindings.createAutoBinding( org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                                                                  this,
                                                                  org.jdesktop.beansbinding.ELProperty.create( "${option.description}" ),
                                                                  description,
                                                                  org.jdesktop.beansbinding.BeanProperty.create( "text" ) );
        binding.setSourceNullValue( "Option Description" );
        binding.setSourceUnreadableValue( "Option Description" );
        bindingGroup.addBinding( binding );

        lengthLabel.setFont( new java.awt.Font( "Dialog", 0, 12 ) ); // NOI18N
        lengthLabel.setText( "Length:" );

        binding =
            org.jdesktop.beansbinding.Bindings.createAutoBinding( org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                                                                  value,
                                                                  org.jdesktop.beansbinding.ELProperty.create( "${text}" ),
                                                                  maxValue,
                                                                  org.jdesktop.beansbinding.BeanProperty.create( "text" ),
                                                                  option.getName() );
        binding.setSourceNullValue( "0" );
        binding.setSourceUnreadableValue( "0" );
        binding.setConverter( new org.jdesktop.beansbinding.Converter<String, String>()
        {

            @Override
            public String convertForward( String value )
            {
                return String.format( "%d", value.length() );
            }

            @Override
            public String convertReverse( String value )
            {
                return "### Read Only ###";
            }
        } );
        bindingGroup.addBinding( binding );

        binding =
            org.jdesktop.beansbinding.Bindings.createAutoBinding( org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                                                                  this,
                                                                  org.jdesktop.beansbinding.ELProperty.create( "${option.value}" ),
                                                                  value,
                                                                  org.jdesktop.beansbinding.BeanProperty.create( "text" ) );
        binding.setSourceNullValue( "Option Value" );
        binding.setSourceUnreadableValue( "Option Value" );
        binding.setValidator( new Validator<String>()
        {
            @Override
            public Validator<String>.Result validate( String value )
            {
                Validator<String>.Result result = null;
                if ( !getOption().isValid( value ) )
                {
                    result = new Validator.Result( 1, "Value invalid" );
                    setValueBackgroundIfError( true );
                }
                else
                {
                    setValueBackgroundIfError( false );
                }
                return result;
            }
        } );
        bindingGroup.addBinding( binding );

        valueScrollPane.setViewportView( value );

        typeLabel.setFont( new java.awt.Font( "Dialog", 0, 12 ) ); // NOI18N
        typeLabel.setText( "Type:" );

        jLabel1.setText( "Text" );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout( this );
        this.setLayout( layout );
        layout.setHorizontalGroup( layout.createParallelGroup( javax.swing.GroupLayout.Alignment.LEADING ).addGroup( layout.createSequentialGroup().addContainerGap().addGroup( layout.createParallelGroup( javax.swing.GroupLayout.Alignment.LEADING ).addGroup( layout.createSequentialGroup().addGroup( layout.createParallelGroup( javax.swing.GroupLayout.Alignment.LEADING ).addComponent( name ).addComponent( description ).addGroup( layout.createSequentialGroup().addComponent( typeLabel ).addPreferredGap( javax.swing.LayoutStyle.ComponentPlacement.RELATED ).addComponent( jLabel1 ).addPreferredGap( javax.swing.LayoutStyle.ComponentPlacement.RELATED ).addComponent( lengthLabel ).addPreferredGap( javax.swing.LayoutStyle.ComponentPlacement.RELATED ).addComponent( maxValue ) ) ).addGap( 0,
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  156,
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  Short.MAX_VALUE ) ).addComponent( valueScrollPane ) ).addContainerGap() ) );
        layout.setVerticalGroup( layout.createParallelGroup( javax.swing.GroupLayout.Alignment.LEADING ).addGroup( layout.createSequentialGroup().addContainerGap().addComponent( name ).addPreferredGap( javax.swing.LayoutStyle.ComponentPlacement.RELATED ).addComponent( valueScrollPane,
                                                                                                                                                                                                                                                                             javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                                                                                                                                                                             141,
                                                                                                                                                                                                                                                                             javax.swing.GroupLayout.PREFERRED_SIZE ).addPreferredGap( javax.swing.LayoutStyle.ComponentPlacement.RELATED ).addGroup( layout.createParallelGroup( javax.swing.GroupLayout.Alignment.BASELINE ).addComponent( lengthLabel ).addComponent( maxValue ).addComponent( typeLabel ).addComponent( jLabel1 ) ).addPreferredGap( javax.swing.LayoutStyle.ComponentPlacement.RELATED ).addComponent( description ).addContainerGap( javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           Short.MAX_VALUE ) ) );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents
     // Variables declaration - do not modify//GEN-BEGIN:variables

    private javax.swing.JLabel description;

    private javax.swing.JLabel jLabel1;

    private javax.swing.JLabel lengthLabel;

    private javax.swing.JLabel maxValue;

    private javax.swing.JLabel name;

    private javax.swing.JLabel typeLabel;

    private javax.swing.JTextPane value;

    private javax.swing.JScrollPane valueScrollPane;

    private org.jdesktop.beansbinding.BindingGroup bindingGroup;

    // End of variables declaration//GEN-END:variables

    @Override
    public void bindingDoUnbind()
    {
        bindingGroup.unbind();
    }

    @Override
    public AbstractOption getUsedOption()
    {
        return option;
    }
}