/*-- 

 Copyright (C) 2012 Jason Hunter & Brett McLaughlin.
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:

 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions, and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions, and the disclaimer that follows 
    these conditions in the documentation and/or other materials 
    provided with the distribution.

 3. The name "JDOM" must not be used to endorse or promote products
    derived from this software without prior written permission.  For
    written permission, please contact <request_AT_jdom_DOT_org>.

 4. Products derived from this software may not be called "JDOM", nor
    may "JDOM" appear in their name, without prior written permission
    from the JDOM Project Management <request_AT_jdom_DOT_org>.

 In addition, we request (but do not require) that you include in the 
 end-user documentation provided with the redistribution and/or in the 
 software itself an acknowledgement equivalent to the following:
     "This product includes software developed by the
      JDOM Project (http://www.jdom.org/)."
 Alternatively, the acknowledgment may be graphical using the logos 
 available at http://www.jdom.org/images/logos.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED.  IN NO EVENT SHALL THE JDOM AUTHORS OR THE PROJECT
 CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 SUCH DAMAGE.

 This software consists of voluntary contributions made by many 
 individuals on behalf of the JDOM Project and was originally 
 created by Jason Hunter <jhunter_AT_jdom_DOT_org> and
 Brett McLaughlin <brett_AT_jdom_DOT_org>.  For more information
 on the JDOM Project, please see <http://www.jdom.org/>.

 */

package org.jdom2.output.support;

import java.util.List;

import org.jdom2.Content;
import org.jdom2.Text;
import org.jdom2.Verifier;

/**
 * This Walker implementation will produce trimmed text content.
 * 
 * @author Rolf Lear
 *
 */
public class WalkTrimmingFullWhite extends AbstractFormattedWalker {

	/**
	 * Create the Trimmed walker instance.
	 * @param content The list of content to format
	 * @param fstack The current stack.
	 * @param escape Whether Text values should be escaped.
	 */
	public WalkTrimmingFullWhite(final List<? extends Content> content, 
			final FormatStack fstack, final boolean escape) {
		super(content, fstack, escape);
	}

	@Override
	protected void analyzeMultiText(final MultiText mtext,
			final int offset, final int len) {
		int ln = len;
		while (--ln >= 0) {
			final Content c = get(offset + ln);
			if (c instanceof Text) {
				// either Text or CDATA
				if (!Verifier.isAllXMLWhitespace(c.getValue())) {
					break;
				}
			} else {
				break;
			}
		}
		if (ln < 0) {
			// all whitespace.
			return;
		}
		
		// some non-white, so return all, but merge the sequential Text items.
		for (int i = 0; i < len; i++) {
			final Content c = get(offset + i);
			switch (c.getCType()) {
				case Text :
					mtext.appendText(Trim.NONE, c.getValue());
					break;
				case CDATA :
					mtext.appendCDATA(Trim.NONE, c.getValue());
					break;
				case EntityRef:
					// treat like any other content.
					// raw.
				default:
					mtext.appendRaw(c);
					break;
			}
		}
		
	}
}
