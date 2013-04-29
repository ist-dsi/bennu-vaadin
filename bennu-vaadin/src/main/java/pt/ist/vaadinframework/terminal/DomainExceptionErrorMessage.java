package pt.ist.vaadinframework.terminal;

import pt.ist.bennu.core.domain.exceptions.DomainException;

import com.vaadin.data.Buffered;
import com.vaadin.data.Buffered.SourceException;
import com.vaadin.terminal.ErrorMessage;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;

public class DomainExceptionErrorMessage extends SourceException {
    public DomainExceptionErrorMessage(Buffered source, DomainException cause) {
        super(source, cause);
    }

    /**
     * @see com.vaadin.terminal.ErrorMessage#getErrorLevel()
     */
    @Override
    public final int getErrorLevel() {
        return ErrorMessage.ERROR;
    }

    /**
     * @see com.vaadin.terminal.Paintable#paint(com.vaadin.terminal.PaintTarget)
     */
    @Override
    public void paint(PaintTarget target) throws PaintException {
        target.startTag("error");
        target.addAttribute("level", "error");

        StringBuilder sb = new StringBuilder();
        final String message = getCause().getLocalizedMessage();
        if (message != null) {
            sb.append("<p>");
            sb.append(message);
            sb.append("</p>");
        }

        target.addXMLSection("div", sb.toString(), "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd");

        target.endTag("error");
    }
}
