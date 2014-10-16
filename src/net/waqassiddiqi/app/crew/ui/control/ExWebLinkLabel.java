package net.waqassiddiqi.app.crew.ui.control;

import com.alee.extended.label.WebLinkLabel;
import com.alee.utils.WebUtils;

public class ExWebLinkLabel extends WebLinkLabel {
	private static final long serialVersionUID = -61407472263877828L;
	
	public void setEmailLink (final String text, final String email, final String subject, final String body) {
		setIcon(EMAIL_ICON);
        setText(text);
        this.link = createEmailLink(email, subject, body);
    }
	
	protected Runnable createEmailLink(final String email , final String subject, final String body) {
		if (email != null) {
			return new Runnable() {
				@Override
				public void run() {
					executorService.execute(new Runnable() {
						@Override
						public void run() {
							WebUtils.writeEmailSafely(email, subject, body);
						}
					});
				}
			};
		} else {
			return null;
		}
	}
}
