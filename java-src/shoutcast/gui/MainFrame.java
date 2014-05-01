package shoutcast.gui;

import java.awt.EventQueue;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.JCheckBox;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class MainFrame extends JFrame {

	private JPanel contentPane;
	private JLabel lblNewLabel;
	private boolean isPlaying = true;
	private JCheckBox chckbxMuteUntilNext;
	private JCheckBox chckbxSaveSong;
	private static ImageIcon pauseIcon, playIcon;
	static {
		try {
			playIcon = new ImageIcon(ImageIO.read(MainFrame.class.getResource("/play2.png")));
			pauseIcon = new ImageIcon(ImageIO.read(MainFrame.class.getResource("/pause2.png")));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		final Notify notify = new Notify() {
			public void setTempMute(boolean mute) {}
			public void setPlay(boolean b) {}
			public void setSave(boolean b) {}
			public void setVolume(double d) {}
			public void setBoost(boolean boost) {}
		};
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame frame = new MainFrame(notify, 0.8);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	public void setSong(String song) {
		lblNewLabel.setText("Song: " + song);
		chckbxMuteUntilNext.setSelected(false);
		chckbxSaveSong.setSelected(false);
	}

	/**
	 * Create the frame.
	 */
	public MainFrame(final Notify notify, double initGain) {
		setTitle("Internet Radio Player");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JLabel lblPlayingChilltrax = new JLabel("Station: chilltrax");
		
		final JButton ppButton = new JButton(pauseIcon);
		ppButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				isPlaying = !isPlaying;
				notify.setPlay(isPlaying);
				ppButton.setIcon(isPlaying ? pauseIcon : playIcon);
				
				chckbxMuteUntilNext.setEnabled(isPlaying);
				chckbxMuteUntilNext.setSelected(false);
			}
		});
		
		final JSlider slider = new JSlider(0, 100, (int) (initGain * 100));
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				notify.setVolume(slider.getValue() / 100.);
			}
		});
		
		slider.setOrientation(SwingConstants.VERTICAL);
		
		lblNewLabel = new JLabel("New label");
		
		chckbxSaveSong = new JCheckBox("Save Song");
		chckbxSaveSong.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				notify.setSave(chckbxSaveSong.isSelected());
			}
		});
		
		chckbxMuteUntilNext = new JCheckBox("Mute until next song");
		chckbxMuteUntilNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				notify.setTempMute(chckbxMuteUntilNext.isSelected());
			}
		});
		
		final JCheckBox chckbxBoostBass = new JCheckBox("Boost Bass");
		chckbxBoostBass.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				notify.setBoost(chckbxBoostBass.isSelected());
			}
		});
		chckbxBoostBass.setSelected(true);
		
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(lblPlayingChilltrax)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(ppButton)
							.addGap(18)
							.addComponent(slider, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addComponent(lblNewLabel)
						.addComponent(chckbxSaveSong)
						.addComponent(chckbxMuteUntilNext)
						.addComponent(chckbxBoostBass))
					.addContainerGap(271, Short.MAX_VALUE))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblPlayingChilltrax)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING, false)
						.addComponent(slider, 0, 0, Short.MAX_VALUE)
						.addComponent(ppButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(lblNewLabel)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(chckbxSaveSong)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(chckbxMuteUntilNext)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(chckbxBoostBass)
					.addContainerGap(49, Short.MAX_VALUE))
		);
		contentPane.setLayout(gl_contentPane);
	}
}
