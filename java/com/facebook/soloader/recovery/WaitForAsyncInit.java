/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.facebook.soloader.recovery;

import com.facebook.soloader.AsyncInitSoSource;
import com.facebook.soloader.LogUtil;
import com.facebook.soloader.SoLoader;
import com.facebook.soloader.SoLoaderULError;
import com.facebook.soloader.SoSource;

public class WaitForAsyncInit implements RecoveryStrategy {
  @Override
  public boolean recover(UnsatisfiedLinkError e, SoSource[] soSources) {
    String soName = null;
    if (e instanceof SoLoaderULError) {
      SoLoaderULError err = (SoLoaderULError) e;
      soName = err.getSoName();
    }

    LogUtil.e(
        SoLoader.TAG,
        "Waiting on SoSources due to "
            + e.getMessage()
            + ((soName == null) ? "" : (", retrying for specific library " + soName)));

    for (SoSource soSource : soSources) {
      if (soSource instanceof AsyncInitSoSource) {
        AsyncInitSoSource source = (AsyncInitSoSource) soSource;
        LogUtil.e(SoLoader.TAG, "Waiting on SoSource " + source.getClass().getName());
        source.waitUntilInitCompleted();
      }
    }
    return true;
  }
}
