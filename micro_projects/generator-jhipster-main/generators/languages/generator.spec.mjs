/**
 * Copyright 2013-2023 the original author or authors from the JHipster project.
 *
 * This file is part of the JHipster project, see https://www.jhipster.tech/
 * for more information.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import { jestExpect as expect } from 'mocha-expect-snapshot';
import lodash from 'lodash';
import { basename, dirname } from 'path';
import { fileURLToPath } from 'url';

import { defaultHelpers as helpers, result as runResult } from '../../test/support/helpers.mjs';
import { shouldSupportFeatures, testBlueprintSupport } from '../../test/support/tests.mjs';
import Generator from './index.mjs';
import { GENERATOR_LANGUAGES } from '../generator-list.mjs';

const { snakeCase } = lodash;

const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);

const generator = basename(__dirname);

describe(`generator - ${generator}`, () => {
  it('generator-list constant matches folder name', async () => {
    await expect((await import('../generator-list.mjs'))[`GENERATOR_${snakeCase(generator).toUpperCase()}`]).toBe(generator);
  });
  shouldSupportFeatures(Generator);
  describe('blueprint support', () => testBlueprintSupport(generator));
  describe('languages migration', () => {
    describe('indonesian language', () => {
      before(() =>
        helpers
          .runJHipster(GENERATOR_LANGUAGES)
          .withSkipWritingPriorities()
          .withJHipsterConfig({
            jhipsterVersion: '7.9.3',
            enableTranslation: true,
            nativeLanguage: 'in',
            languages: ['in'],
            baseName: 'jhipster',
          })
      );
      it('should migrate in language to id', () => {
        runResult.assertJsonFileContent('.yo-rc.json', {
          'generator-jhipster': {
            nativeLanguage: 'id',
            languages: ['id'],
          },
        });
      });
    });
  });
});