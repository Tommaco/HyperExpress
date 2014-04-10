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
package com.strategicgains.hyperexpress.domain;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;

import com.strategicgains.hyperexpress.ResourceException;

/**
 * @author toddf
 * @since Apr 7, 2014
 */
public abstract class AbstractResource
extends HashMap<String, Object>
implements Resource
{
	private static final long serialVersionUID = 6406167608894764116L;
	public static final int IGNORED_FIELD_MODIFIERS = Modifier.FINAL | Modifier.STATIC | Modifier.TRANSIENT | Modifier.VOLATILE;

	@Override
	public Resource withFields(Object object)
	{
		processFields(object.getClass(), object);
		return this;
	}

	private void processFields(Class<?> type, Object object)
	{
		if (type == null) return;

		Field[] fields = getDeclaredFields(type);

		try
		{
			for (Field f : fields)
			{
				// TODO: check for desired annotations, exclusions, modifiers,
				// etc. instead of hard-coding
				if ((f.getModifiers() & IGNORED_FIELD_MODIFIERS) == 0)
				{
					f.setAccessible(true);
					withProperty(f.getName(), f.get(object));
				}
			}
		}
		catch (IllegalAccessException e)
		{
			throw new ResourceException(e);
		}

		processFields(type.getSuperclass(), object);
	}

	private Field[] getDeclaredFields(Class<?> type)
	{
		return type.getDeclaredFields();
	}

	@Override
	public Resource withProperty(String name, Object value)
	{
		if (containsKey(name))
		{
			throw new ResourceException("Duplicate property: " + name);
		}

		put(name, value);
		return this;
	}

	@Override
	public Resource withLink(String rel, String url, String title, String type)
	{
		LinkDefinition ld = new LinkDefinition(rel, url);
		ld.set("title", title);
		ld.set("type", type);
		return withLink(ld);
	}
}
