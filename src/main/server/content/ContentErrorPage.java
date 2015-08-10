package main.server.content;

public class ContentErrorPage extends ContentPage {

	public ContentErrorPage(String errorMsg) {
		ContentGroup group = new ContentGroup();
		group.add(new ContentText(10, 10, errorMsg, ContentText.TextType.SUBTITLE));
		addContentGroup(group);
	}
}
