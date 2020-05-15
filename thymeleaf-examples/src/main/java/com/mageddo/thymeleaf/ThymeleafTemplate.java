package com.mageddo.thymeleaf;

import java.io.StringWriter;
import java.util.Locale;
import java.util.Map;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

public class ThymeleafTemplate {

  private TemplateEngine templateEngine;

  public ThymeleafTemplate() {
    this(true);
  }

  public ThymeleafTemplate(boolean cacheable) {
    this.templateEngine = new TemplateEngine();
    final ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
    templateResolver.setCacheable(cacheable);
    templateEngine.setTemplateResolver(templateResolver);
  }

  public String fromPath(String templateName, Map<String, Object> variables) {
    return this.fromPath(templateName, new Context(Locale.getDefault(), variables));
  }

  public String fromPath(String templateName, Context context) {
    return this.from(templateName, context);
  }

  public String from(String template, Map<String, Object> variables) {
    return from(template, new Context(Locale.getDefault(), variables));
  }

  public String from(String template, Context context) {
    StringWriter stringWriter = new StringWriter();
    this.templateEngine.process(template, context, stringWriter);
    return stringWriter.toString();
  }

}
