package com.github.strangefac.doegac;

import java.io.File;
import java.io.IOException;
import org.apache.maven.enforcer.rule.api.EnforcerRule;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import jdepend.framework.JDepend;

public class DOEGAC implements EnforcerRule {
  public void execute(EnforcerRuleHelper helper) throws EnforcerRuleException {
    try {
      Log log = helper.getLog();
      JDepend jDepend = new JDepend();
      for (String key : new String[]{"project.build.outputDirectory", "project.build.testOutputDirectory"}) {
        File dir = new File((String) helper.evaluate("${" + key + '}'));
        if (dir.exists()) {
          log.info(String.format("Adding %s to JDepend analysis.", dir));
          jDepend.addDirectory(dir.getPath());
        } else {
          log.info(String.format("Not adding %s to JDepend as it does not exist.", dir));
        }
      }
      jDepend.analyze();
      if (jDepend.containsCycles()) throw new EnforcerRuleException("Package cycles! Use e.g. JDepend4Eclipse to find out which packages are involved.");
      log.info("Success: No cycles in package dependency graph.");
    } catch (ExpressionEvaluationException e) {
      throw new RuntimeException("Oops:", e);
    } catch (IOException e) {
      throw new RuntimeException("Oops:", e);
    }
  }

  public boolean isCacheable() {
    return false; // XXX: Maybe it should be?
  }

  public String getCacheId() {
    throw new UnsupportedOperationException("Did not expect this to be called.");
  }

  public boolean isResultValid(EnforcerRule cachedRule) {
    throw new UnsupportedOperationException("Did not expect this to be called.");
  }
}
