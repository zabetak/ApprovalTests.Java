package org.approvaltests.utils;

import com.spun.util.ArrayUtils;
import org.approvaltests.core.Options;
import org.approvaltests.core.Verifiable;
import org.approvaltests.core.VerifyParameters;
import org.approvaltests.strings.MarkdownCompatible;
import org.lambda.functions.Function1;
import org.lambda.query.Queryable;

public class MarkdownTable extends MarkdownTableBasic implements Verifiable
{
  public MarkdownTable(MarkdownTableBasic markdownTableBasic) {
    this.markdown = markdownTableBasic.toMarkdown();
  }

  public static <I, O> MarkdownTable create(I[] inputs, Function1<I, O> o, String column1, String column2)
  {
    return new MarkdownTable(MarkdownTableBasic.create(inputs, o, column1, column2));
  }
  @Override
  public VerifyParameters getVerifyParameters(Options options)
  {
    return new VerifyParameters(options.forFile().withExtension(".md"));
  }
}
