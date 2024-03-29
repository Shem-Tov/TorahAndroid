package com.torah.sinai.moses.torahandroid.engine.frame;


public class HighLighter {

	private String findWord = null;
	private int pos = 0;
	private static HighLighter instanceTextPane;
	private static HighLighter instanceTorahPane;
	private JTextPane textComp;
	
	public static HighLighter getInstance(JTextPane tPane, Boolean isText) {
		if (isText) {
			if (instanceTextPane == null) {
				instanceTextPane = new HighLighter(tPane);
			}
			return instanceTextPane;
		} else {
			if (instanceTorahPane == null) {
				instanceTorahPane = new HighLighter(tPane);
			}
			return instanceTorahPane;
		}
	}

	public HighLighter(JTextPane tPane) {
		textComp = tPane;
	}
	
	// Creates highlights around all occurrences of pattern in textComp
	public void highlight(String pattern) {
		// First remove all old highlights
		removeHighlights();
		try {
			pos = 0;
			findWord = pattern;
			Highlighter hilite = textComp.getHighlighter();
			Document doc = textComp.getDocument();
			String text = doc.getText(0, doc.getLength());
			int pos = 0;

			// Search for pattern
			// see I have updated now its not case sensitive
			while ((pos = text.toUpperCase().indexOf(pattern.toUpperCase(), pos)) >= 0) {
				// Create highlighter using private painter and apply around pattern
				hilite.addHighlight(pos, pos + pattern.length(), myHighlightPainter);
				pos += pattern.length();
			}
			scrollWords();
		} catch (BadLocationException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	// Removes only our private highlights
	public void removeHighlights() {
		findWord = null;
		Highlighter hilite = textComp.getHighlighter();
		Highlighter.Highlight[] hilites = hilite.getHighlights();
		for (int i = 0; i < hilites.length; i++) {
			if (hilites[i].getPainter() instanceof MyHighlightPainter) {
				hilite.removeHighlight(hilites[i]);
			}
		}
	}

	// An instance of the private subclass of the default highlight painter
	//HighLighter outer = new HighLighter(null);
	MyHighlightPainter myHighlightPainter = this.new MyHighlightPainter(new java.awt.Color(245, 230, 210));

	// A private subclass of the default highlight painter
	class MyHighlightPainter extends DefaultHighlighter.DefaultHighlightPainter {
		public MyHighlightPainter(Color color) {
			super(color);
		}
	}

	public void scrollWords() {
		// Focus the text area, otherwise the highlighting won't show up
		textComp.requestFocusInWindow();
		// Make sure we have a valid search term
		if (findWord != null && findWord.length() > 0) {
			String find = findWord.toLowerCase();
			Document document = textComp.getDocument();
			int findLength = find.length();
			try {
				boolean found = false;
				// Rest the search position if we're at the end of the document
				if (pos + findLength > document.getLength()) {
					pos = 0;
				}
				// While we haven't reached the end...
				// "<=" Correction
				while (pos + findLength <= document.getLength()) {
					// Extract the text from teh docuemnt
					String match = document.getText(pos, findLength).toLowerCase();
					// Check to see if it matches or request
					if (match.equals(find)) {
						found = true;
						break;
					}
					pos++;
				}

				// Did we find something...
				if (found) {
					// Get the rectangle of the where the text would be visible...
					Rectangle viewRect = textComp.modelToView(pos);
					// Scroll to make the rectangle visible
					textComp.scrollRectToVisible(viewRect);
					// Highlight the text
					textComp.setCaretPosition(pos + findLength);
					textComp.moveCaretPosition(pos);
					// Move the search position beyond the current match
					pos += findLength;
				}

			} catch (Exception exp) {
				exp.printStackTrace();
			}
		}
	}
}
