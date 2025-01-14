// Copyright 2014 The Bazel Authors. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.devtools.build.lib.analysis.configuredtargets;

import com.google.common.base.Preconditions;
import com.google.devtools.build.lib.actions.Artifact;
import com.google.devtools.build.lib.actions.Artifact.SourceArtifact;
import com.google.devtools.build.lib.analysis.TargetContext;
import com.google.devtools.build.lib.collect.nestedset.NestedSet;
import com.google.devtools.build.lib.collect.nestedset.NestedSetBuilder;
import com.google.devtools.build.lib.collect.nestedset.Order;
import com.google.devtools.build.lib.concurrent.ThreadSafety.Immutable;
import com.google.devtools.build.lib.packages.InputFile;
import com.google.devtools.build.lib.packages.License;
import java.util.Objects;
import javax.annotation.Nullable;
import net.starlark.java.eval.Printer;

/**
 * A ConfiguredTarget for an InputFile.
 *
 * <p>All InputFiles for the same target are equivalent, so configuration does not play any role
 * here and is always set to <b>null</b>.
 */
@Immutable
public final class InputFileConfiguredTarget extends FileConfiguredTarget {
  private final SourceArtifact artifact;
  private final NestedSet<TargetLicense> licenses;

  public InputFileConfiguredTarget(
      TargetContext targetContext, InputFile inputFile, SourceArtifact artifact) {
    super(
        targetContext.getAnalysisEnvironment().getOwner(),
        targetContext.getVisibility(),
        artifact,
        /* instrumentedFilesInfo= */ null,
        /* configFragmentsProvider= */ null,
        /* generatingRuleOutputGroupInfo= */ null);
    Preconditions.checkArgument(getConfigurationKey() == null, getLabel());
    Preconditions.checkArgument(targetContext.getTarget() == inputFile, getLabel());
    this.artifact = artifact;
    this.licenses = makeLicenses(inputFile);
  }

  private static NestedSet<TargetLicense> makeLicenses(InputFile inputFile) {
    License license = inputFile.getLicense();
    return Objects.equals(license, License.NO_LICENSE)
        ? NestedSetBuilder.emptySet(Order.LINK_ORDER)
        : NestedSetBuilder.create(
            Order.LINK_ORDER, new TargetLicense(inputFile.getLabel(), license));
  }

  @Override
  public final Artifact getArtifact() {
    return artifact;
  }

  @Override
  public String toString() {
    return "InputFileConfiguredTarget(" + getLabel() + ")";
  }

  @Override
  public final NestedSet<TargetLicense> getTransitiveLicenses() {
    return licenses;
  }

  @Override
  @Nullable
  public TargetLicense getOutputLicenses() {
    return null;
  }

  @Override
  public boolean hasOutputLicenses() {
    return false;
  }

  @Override
  public void repr(Printer printer) {
    printer.append("<input file target " + getLabel() + ">");
  }

  public SourceArtifact getSourceArtifact() {
    return artifact;
  }
}
