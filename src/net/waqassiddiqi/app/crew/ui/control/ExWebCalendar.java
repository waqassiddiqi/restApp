package net.waqassiddiqi.app.crew.ui.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Calendar;
import java.util.Date;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;

import com.alee.extended.date.WebCalendar;
import com.alee.global.StyleConstants;
import com.alee.laf.button.WebButton;
import com.alee.laf.button.WebToggleButton;
import com.alee.laf.separator.WebSeparator;
import com.alee.utils.TimeUtils;

public class ExWebCalendar extends WebCalendar {
	private static final long serialVersionUID = 1L;

	public ExWebCalendar(Date currentDate) {
		super(currentDate);
	}

	@Override
	protected void updateMonth (final JPanel monthDays) {
		monthDays.removeAll();
		lastSelectedDayButton = null;

		monthDays.add(new WebSeparator(WebSeparator.VERTICAL), "1,0,1,5");
		monthDays.add(new WebSeparator(WebSeparator.VERTICAL), "3,0,3,5");
		monthDays.add(new WebSeparator(WebSeparator.VERTICAL), "5,0,5,5");
		monthDays.add(new WebSeparator(WebSeparator.VERTICAL), "7,0,7,5");
		monthDays.add(new WebSeparator(WebSeparator.VERTICAL), "9,0,9,5");
		monthDays.add(new WebSeparator(WebSeparator.VERTICAL), "11,0,11,5");

		final ButtonGroup dates = new ButtonGroup();

		Calendar calActualCurrentDate = Calendar.getInstance();
		int actualCurrentYear = calActualCurrentDate.get(Calendar.YEAR);
		int actualCurrentMonth = calActualCurrentDate.get(Calendar.MONTH);

		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(shownDate);
		calendar.set(Calendar.DAY_OF_MONTH, 1);

		int col = 0;
		int row = 0;

		// Month before
		final int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		final int shift;
		switch (dayOfWeek) {
		case Calendar.MONDAY:
			shift = startWeekFromSunday ? 1 : 7;
			break;
		case Calendar.TUESDAY:
			shift = startWeekFromSunday ? 2 : 1;
			break;
		case Calendar.WEDNESDAY:
			shift = startWeekFromSunday ? 3 : 2;
			break;
		case Calendar.THURSDAY:
			shift = startWeekFromSunday ? 4 : 3;
			break;
		case Calendar.FRIDAY:
			shift = startWeekFromSunday ? 5 : 4;
			break;
		case Calendar.SATURDAY:
			shift = startWeekFromSunday ? 6 : 5;
			break;
		case Calendar.SUNDAY:
			shift = startWeekFromSunday ? 7 : 6;
			break;
		default:
			shift = 0;
			break;
		}
		TimeUtils.changeByDays(calendar, -shift);
		while (calendar.get(Calendar.DAY_OF_MONTH) > 1) {
			final Date thisDate = calendar.getTime();
			final WebToggleButton day = new WebToggleButton();
			day.setForeground(otherMonthForeground);
			day.setText("" + calendar.get(Calendar.DAY_OF_MONTH));
			day.setRolloverDecoratedOnly(true);
			day.setHorizontalAlignment(WebButton.RIGHT);
			day.setRound(StyleConstants.smallRound);
			day.setFocusable(false);
			day.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(final ItemEvent e) {
					final WebToggleButton dayButton = (WebToggleButton) e
							.getSource();
					if (dayButton.isSelected()) {
						setDateImpl(thisDate);
					}
				}
			});
			if (dateCustomizer != null) {
				dateCustomizer.customize(day, thisDate);
			}
			monthDays.add(day, col * 2 + "," + row);

			int thisCurrentYear = calendar.get(Calendar.YEAR);
			int thisCurrentMonth = calendar.get(Calendar.MONTH);

			if (actualCurrentYear < thisCurrentYear
					|| ((actualCurrentYear >= thisCurrentYear) && actualCurrentMonth < thisCurrentMonth)) {
				day.setEnabled(false);
			}
			day.setEnabled(false);
			dates.add(day);

			TimeUtils.increaseByDay(calendar);

			col++;
			if (col > 6) {
				col = 0;
				row++;
			}
		}

		// Current month
		do {
			final boolean weekend = calendar.get(Calendar.DAY_OF_WEEK) == 1
					|| calendar.get(Calendar.DAY_OF_WEEK) == 7;
			final boolean selected = date != null
					&& TimeUtils.isSameDay(calendar, date.getTime());

			final Date thisDate = calendar.getTime();
			final WebToggleButton day = new WebToggleButton();
			day.setForeground(weekend ? weekendsForeground
					: currentMonthForeground);
			day.setText("" + calendar.get(Calendar.DAY_OF_MONTH));
			day.setSelected(selected);
			day.setRolloverDecoratedOnly(true);
			day.setHorizontalAlignment(WebButton.RIGHT);
			day.setRound(StyleConstants.smallRound);
			day.setFocusable(false);
			day.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					lastSelectedDayButton = (WebToggleButton) e.getSource();
					setDateImpl(thisDate);
				}
			});
			if (dateCustomizer != null) {
				dateCustomizer.customize(day, thisDate);
			}
			monthDays.add(day, col * 2 + "," + row);

			int thisCurrentYear = calendar.get(Calendar.YEAR);
			int thisCurrentMonth = calendar.get(Calendar.MONTH);

			if (actualCurrentYear < thisCurrentYear
					|| ((actualCurrentYear >= thisCurrentYear) && actualCurrentMonth < thisCurrentMonth)) {
				day.setEnabled(false);
			}

			dates.add(day);

			if (selected) {
				lastSelectedDayButton = day;
			}

			TimeUtils.increaseByDay(calendar);

			col++;
			if (col > 6) {
				col = 0;
				row++;
			}
		} while (calendar.get(Calendar.DAY_OF_MONTH) > 1);

		// Month after
		final int left = 6 * 7 - (monthDays.getComponentCount() - 6);
		for (int i = 1; i <= left; i++) {
			final Date thisDate = calendar.getTime();
			final WebToggleButton day = new WebToggleButton();
			day.setForeground(otherMonthForeground);
			day.setText("" + calendar.get(Calendar.DAY_OF_MONTH));
			day.setRolloverDecoratedOnly(true);
			day.setHorizontalAlignment(WebButton.RIGHT);
			day.setRound(StyleConstants.smallRound);
			day.setFocusable(false);
			day.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(final ItemEvent e) {
					final WebToggleButton dayButton = (WebToggleButton) e
							.getSource();
					if (dayButton.isSelected()) {
						setDateImpl(thisDate);
					}
				}
			});
			if (dateCustomizer != null) {
				dateCustomizer.customize(day, thisDate);
			}
			monthDays.add(day, col * 2 + "," + row);

			day.setEnabled(false);

			dates.add(day);

			TimeUtils.increaseByDay(calendar);

			col++;
			if (col > 6) {
				col = 0;
				row++;
			}
		}

		monthDays.revalidate();
	}
}
