package com.strategicgains.hyperexpress.builder;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.strategicgains.hyperexpress.domain.Link;
import com.strategicgains.hyperexpress.domain.LinkDefinition;

/**
 * Build LinkDefinition instances from a URL pattern, binding URL tokens to
 * actual values. Uses UrlBuilder to build the href or URL portion of the 
 * Link.
 * 
 * @author toddf
 * @since May 5, 2014
 * @see UrlBuilder
 */
public class LinkBuilder
{
	private static final String REL_TYPE = "rel";
	private static final String TITLE = "title";
	private static final String TYPE = "type";

	private UrlBuilder urlBuilder;
	private Map<String, String> attributes = new HashMap<String, String>();

	/**
	 * Create an empty LinkBuilder, with no URL pattern. Using this constructor
	 * mandates that you MUST use the build(String) form of build instead of the
	 * parameterless, build() or other forms, as the latter will throw
	 * IllegalStateException in this state, due to not having a URL in the
	 * generated link.
	 */
	public LinkBuilder()
	{
		super();
		urlBuilder = new UrlBuilder();
	}

	/**
	 * Create a new LinkBuilder, passing in the URL pattern in which to
	 * substitute tokens. This URL pattern may represent the entire URL or just
	 * the path portion (relative path).
	 * <p/>
	 * The URL pattern is templated, in that it contains tokens to be later
	 * substituted for actual values. The tokens are delimited with beginning
	 * and trailing curly-braces (e.g. '{token}').
	 * <p/>
	 * If used in conjunction with baseUrl(), this URL pattern must be just the
	 * path portion of the URL and should be prefixed with a leading slash
	 * ('/').
	 * <p/>
	 * For example: '/users/{userId}' or
	 * 'http://www.example.com/api/users/{userId}'
	 * 
	 * @param urlPattern a URL path with optional tokens of the form '{tokenName}'
	 */
	public LinkBuilder(String urlPattern)
	{
		super();
		urlBuilder = new UrlBuilder(urlPattern);
	}

	public LinkBuilder(LinkBuilder that)
	{
		super();
		this.urlBuilder = that.urlBuilder.clone();
		this.attributes = new HashMap<String, String>(that.attributes);
	}

	/**
	 * Set the prefix portion of the URL which is to be pre-pended to the URL
	 * pattern.
	 * <p/>
	 * Optional, as the URL pattern may contain everything. However, this is
	 * provided as a convenience so consumers don't have to perform their own
	 * concatenation to pass in the entire URL pattern string to the constructor.
	 * <p/>
	 * For example: 'http://www.example.com:8080'
	 * 
	 * @param baseUrl the string that will prefix the URL pattern
	 * @return this LinkBuilder instance to facilitate method chaining.
	 */
	public LinkBuilder baseUrl(String url)
	{
		urlBuilder.baseUrl(url);
		return this;
	}

	/**
	 * Add an optional query-string segment to this LinkBuilder.
	 * <p/>
	 * If all of the tokens in the query-string are bound, the segment is included
	 * in the generated URL string during build().  However, if there are unbound
	 * tokens in the resulting query-string segment, it is not included in the
	 * generated URL string.
	 * <p/>
	 * Do not include any question mark ("?") or ampersand ("&") in the query-string
	 * segment.
	 * 
	 * @param query a query-string segment to optionally include.
	 * @return this LinkBuilder instance to facilitate method chaining.
	 */
	public LinkBuilder withQuery(String query)
	{
		urlBuilder.withQuery(query);
		return this;
	}

	/**
	 * Remove all attribute settings from this UrlBuilder.
	 * Properties such as 'rel', 'href', 'type' and 'title'
	 * are removed. Does not clear queries or URL-related
	 * properties, namely baseUrl or URl pattern.
	 */
	public void clearAttributes()
	{
		attributes.clear();
	}

	/**
	 * Remove the query-string segments from this UrlBuilder.
	 */
	public void clearQueries()
	{
		urlBuilder.clearQueries();
	}

	/**
	 * Retrieve the URL pattern associated with this link builder.
	 * 
	 * @return the URL pattern or null.
	 */
	public String urlPattern()
	{
		return urlBuilder.urlPattern();
	}

	public LinkBuilder urlPattern(String pattern)
	{
		urlBuilder.urlPattern(pattern);
		return this;
	}

	/**
	 * Set the 'rel' or relation-type value for links generated by this LinkBuilder.
	 * 
	 * @param rel the relation-type name.
	 * @return this LinkBuilder instance to facilitate method chaining.
	 */
	public LinkBuilder rel(String rel)
	{
		return set(REL_TYPE, rel);
	}

	/**
	 * Retrieve the 'rel' or relation-type name.
	 * 
	 * @return the relation-type name, or null if the value is not set.
	 */
	public String rel()
	{
		return get(REL_TYPE);
	}

	/**
	 * Set the 'title' value of links generated by this LinkBuilder.
	 * 
	 * @param title the title for generated links.
	 * @return this LinkBuilder instance to facilitate method chaining.
	 */
	public LinkBuilder title(String title)
	{
		return set(TITLE, title);
	}

	/**
	 * Retrieve the 'title' value.
	 * 
	 * @return the link title, or null if the value is not set.
	 */
	public String title()
	{
		return get(TITLE);
	}

	/**
	 * Set the 'type' value of links generated by this LinkBuilder.
	 * 
	 * @param type the type for generated links.
	 * @return this LinkBuilder instance to facilitate method chaining.
	 */
	public LinkBuilder type(String type)
	{
		return set(TYPE, type);
	}

	/**
	 * Retrieve the 'type'.
	 * 
	 * @return the type of links, or null if the value is not set.
	 */
	public String type()
	{
		return get(TYPE);
	}

	/**
	 * Retrieve the value of an arbitrary named property value.
	 * 
	 * @param name the name of the property to retrieve.
	 * @return the value for the give name, or null if the property is not set.
	 */
	public String get(String name)
	{
		return attributes.get(name);
	}

	/**
	 * Set an arbitrary named value for links generated by this LinkBuilder.
	 * If a property of the same name was previously-set, including 'rel', 'title'
	 * or 'type', it will be overwritten.
	 * <p/>
	 * If the value is null, the property is removed, if it was set before.
	 * 
	 * @param name the name of a property.
	 * @param value the value of the property given by name.
	 * @return this LinkBuilder instance to facilitate method chaining.
	 */
	public LinkBuilder set(String name, String value)
	{
		if (value == null)
		{
			attributes.remove(name);
		}
		else
		{
			attributes.put(name, value);
		}

		return this;
	}

	public Link build()
	{
		return build(null);
	}

	public Link build(TokenResolver tokenResolver)
	{
		return createLink(urlBuilder.build(tokenResolver));
	}

	/**
	 * Build a Link instance.
	 * 
	 * @param tokenResolver a TokenResolver with token bindings.
	 * @return a new Link instance
	 * @throws LinkBuilderException if the LinkBuilder is in a state to build an invalid
	 * LinkDefintion.
	 */
	public Link build(Object object, TokenResolver tokenResolver)
	{
		return createLink(urlBuilder.build(object, tokenResolver));
	}

	@Override
	public String toString()
	{
		StringBuilder s = new StringBuilder();
		s.append(this.getClass().getSimpleName());
		s.append("{");
		boolean isFirst = true;

		for (Entry<String, String> entry : attributes.entrySet())
		{
			if (!isFirst)
			{
				s.append(", ");
			}
			else
			{
				isFirst = false;
			}

			s.append(entry.getKey());
			s.append("=");
			s.append(entry.getValue());
		}

		s.append("}");
		return s.toString();
	}

	private Link createLink(String url)
	{
		Link link = new LinkDefinition(attributes.get(REL_TYPE), url);

		for (Entry<String, String> entry : attributes.entrySet())
		{
			if (!entry.getKey().equalsIgnoreCase(REL_TYPE))
			{
				link.set(entry.getKey(), entry.getValue());
			}
		}

		return link;
	}
}
