package com.renomad.minum.htmlparsing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represents the expected types of things we may encounter when parsing an HTML string, which
 * for our purposes is {@link ParseNodeType}.
 * <p>
 * See <a href="https://www.w3.org/TR/2011/WD-html-markup-20110113/syntax.html#syntax-elements">W3.org Elements</a>
 * </p>
 */
public record HtmlParseNode(ParseNodeType type,
                            TagInfo tagInfo,
                            List<HtmlParseNode> innerContent,
                            String textContent) {

    public static final HtmlParseNode EMPTY = new HtmlParseNode(ParseNodeType.ELEMENT, TagInfo.EMPTY, List.of(), "EMPTY HTMLPARSENODE");

    /**
     * Return a list of strings of the text content of the tree.
     * <p>
     * This method traverses the tree from this node downwards,
     * adding the text content as it goes. Its main purpose is to
     * quickly render all the strings out of an HTML document at once.
     * </p>
     */
    public List<String> print() {
        var myList = new ArrayList<String>();
        recursiveTreeWalk(myList);
        return myList;
    }

    private void recursiveTreeWalk(ArrayList<String> myList) {
        for (var hpn : innerContent) {
            hpn.recursiveTreeWalk(myList);
        }
        if (textContent != null && ! textContent.isBlank()) {
            myList.add(textContent);
        }
    }

    /**
     * Return a list of {@link HtmlParseNode} nodes in the HTML that match provided attributes.
     */
    public List<HtmlParseNode> search(TagName tagName, Map<String, String> attributes) {
        var myList = new ArrayList<HtmlParseNode>();
        recursiveTreeWalkSearch(myList, tagName, attributes);
        return myList;
    }

    private void recursiveTreeWalkSearch(ArrayList<HtmlParseNode> myList, TagName tagName, Map<String, String> attributes) {
        if (this.tagInfo().tagName().equals(tagName) && this.tagInfo().attributes().entrySet().containsAll(attributes.entrySet())) {
            myList.add(this);
        }
        for (var htmlParseNode : innerContent) {
            htmlParseNode.recursiveTreeWalkSearch(myList, tagName, attributes);
        }
    }

    /**
     * Return the inner text
     * <p>
     * If this element has only one inner
     * content item, and it's a {@link ParseNodeType#CHARACTERS} element, return its text content.
     * </p>
     * <p>
     *     Otherwise, return an empty string.
     * </p>
     */
    public String innerText() {
       if (innerContent.size() == 1 && innerContent.getFirst().type == ParseNodeType.CHARACTERS) {
           return innerContent.getFirst().textContent;
       } else {
           return "";
       }
    }

}