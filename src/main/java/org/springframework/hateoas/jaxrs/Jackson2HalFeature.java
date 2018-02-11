/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.hateoas.jaxrs;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.hateoas.RelProvider;
import org.springframework.hateoas.core.EvoInflectorRelProvider;
import org.springframework.hateoas.hal.HalConfiguration;
import org.springframework.hateoas.hal.Jackson2HalModule;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

/**
 * JAX-RS {@link Feature} used to register a customized {@link ObjectMapper} with {@link Jackson2HalModule}.
 *
 * @author Greg Turnquist
 */
public class Jackson2HalFeature implements Feature {

	static final ObjectMapper mapper;

	static {
		mapper = new ObjectMapper();
		mapper.registerModule(new Jackson2HalModule());
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

		RelProvider relProvider = new EvoInflectorRelProvider();

		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasename("classpath:rest-messages");
		MessageSourceAccessor linkRelationMessageSource = new MessageSourceAccessor(messageSource);

		mapper.setHandlerInstantiator(new Jackson2HalModule.HalHandlerInstantiator(relProvider, null,
			linkRelationMessageSource, new HalConfiguration()));
	}

	@Override
	public boolean configure(FeatureContext context) {

		JacksonJaxbJsonProvider provider = new JacksonJaxbJsonProvider(
			mapper, JacksonJaxbJsonProvider.DEFAULT_ANNOTATIONS);
		context.register(provider);

		return true;
	}
}