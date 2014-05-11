/*
    Copyright 2014, Strategic Gains, Inc.

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

		http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
 */
package com.strategicgains.hyperexpress.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.strategicgains.hyperexpress.util.MapStringFormat;

/**
 * TokenResolver is a utility class that uses UrlBuilder to replace tokens
 * in strings with values. It allows the addition of TokenBinder instances,
 * which can extract token values from Object instances before replacing
 * tokens in a URL.
 * 
 * @author toddf
 * @since Apr 28, 2014
 */
public class TokenResolver
{
	private static final MapStringFormat FORMATTER = new MapStringFormat();

	private Map<String, String> tokenBindings = new HashMap<String, String>();
	private List<TokenBinder> tokenBinders = new ArrayList<TokenBinder>();

	/**
	 * Bind a URL token to a value. During resolve(), any token names matching
	 * the given token name here will be replaced with the given value.
	 * 
	 * Set a value to be substituted for a token in the URL pattern. While
	 * tokens in the URL pattern are delimited with curly-braces, the token name
	 * does not contain the braces. The value is any URL-safe string value.
	 * 
	 * @param tokenName the name of a token in the URL pattern.
	 * @param value the string value to substitute for the token name in the URL pattern.
	 * @return this TokenResolver instance to facilitate method chaining.
	 */
	public TokenResolver bind(String tokenName, String value)
	{
		if (value == null)
		{
			tokenBindings.remove(tokenName);
		}
		else
		{
			tokenBindings.put(tokenName, value);
		}

		return this;
	}

	/**
	 * Removes all bound tokens. Does not remove token binder callbacks.
	 */
	public void clear()
	{
		tokenBindings.clear();
	}

	/**
	 * 'Unbind' a named substitution value from a token name.
	 * 
	 * @param tokenName the name of a previously-bound token name.
	 */
	public void remove(String tokenName)
	{
		tokenBindings.remove(tokenName);
	}

	/**
	 * Install a callback TokenBinder instance. During the resolve() methods that
	 * take an Object instance such as, resolve(String, Object) and
	 * resolve(Collection<String>, Object), the TokenBinder.bind(Object) method
	 * is called to bind additional tokens that may come from the object.
	 * 
	 * @param callback a TokenBinder implementation.
	 * @return this instance of TokenResolver to facilitate method chaining.
	 */
	public TokenResolver addTokenBinder(TokenBinder callback)
	{
		if (callback == null) return this;

		tokenBinders.add(callback);
		return this;
	}

	/**
	 * Removes all bound tokens and token binder callbacks from this
	 * TokenResolver, making it essentially empty. After reset() this
	 * TokenResolver's state is as if it was newly instantiated.
	 */
	public void reset()
	{
		clear();
		tokenBinders.clear();
	}

	/**
	 * Resolve the tokens in the pattern string.
	 * 
	 * @param pattern
	 * @return
	 */
	public String resolve(String pattern)
	{
		return FORMATTER.format(pattern, tokenBindings);
	}

	/**
	 * Resolve the tokens in a URL pattern, binding additional token values from
	 * the given Object first. Any TokenBinder callbacks are called for the
	 * object before resolving the tokens. If object is null, no token binders
	 * are called.
	 * 
	 * @param pattern a pattern string optionally containing tokens.
	 * @param object an instance for which to call TokenBinders.
	 * @return a string with bound tokens substituted for values.
	 */
	public String resolve(String pattern, Object object)
	{
		if (object != null)
		{
			callTokenBinders(object);
		}

		return resolve(pattern);
	}

	/**
	 * Resolve the tokens in the collection of URL patterns, returning a
	 * collection of resolved URLs. The resulting URLs may still contain tokens
	 * if they do not have values bound.
	 * 
	 * @param patterns a list of URL patterns
	 * @return a collection of URLs with bound tokens substituted for values.
	 */
	public Collection<String> resolve(Collection<String> patterns)
	{
		List<String> resolved = new ArrayList<String>(patterns.size());

		for (String pattern : patterns)
		{
			resolved.add(resolve(pattern));
		}

		return resolved;
	}

	/**
	 * Resolve the tokens in a collection of URL patterns, binding additional
	 * token values from the given Object first. Any TokenBinder callbacks are
	 * called for the object before resolving the tokens. If object is null, no
	 * token binders are called. The resulting URLs may still contain tokens if
	 * they do not have values bound.
	 * 
	 * @param patterns a collection of URL patterns optionally containing tokens.
	 * @param object an instance for which to call TokenBinders.
	 * @return a collection of URLs with bound tokens substituted for values.
	 */
	public Collection<String> resolve(Collection<String> patterns, Object object)
	{
		if (object != null)
		{
			callTokenBinders(object);
		}

		return resolve(patterns);
	}

	/**
	 * Call the installed TokenBinder instances, calling bind() for each one
	 * and passing the object so the TokenBinders can extract token values
	 * from the object.
	 * 
	 * @param object an object for which to extract token bindings.
	 */
	private void callTokenBinders(Object object)
	{
		if (object == null) return;

		for (TokenBinder tokenBinder : tokenBinders)
		{
			tokenBinder.bind(object);
		}
	}

	public String toString()
	{
		StringBuilder s = new StringBuilder();
	    s.append("{");
		boolean isFirst = true;

		for (Entry<String, String> entry : tokenBindings.entrySet())
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

		return s.toString();
    }
}
