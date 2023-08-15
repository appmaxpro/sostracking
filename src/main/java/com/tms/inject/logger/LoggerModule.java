/*
 * Axelor Business Solutions
 *
 * Copyright (C) 2005-2023 Axelor (<http://axelor.com>).
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.tms.inject.logger;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;
import org.slf4j.Logger;

/** Provides support for SLF4J Logger injection. */
public final class LoggerModule extends AbstractModule {

  @Override
  protected void configure() {
    bindListener(Matchers.any(), new LoggerProvisionListener());
    bind(Logger.class).toProvider(new LoggerProvider());
  }
}
