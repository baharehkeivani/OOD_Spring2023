import { JHipsterCommandDefinition } from '../base/api.mjs';
import { GENERATOR_APP } from '../generator-list.mjs';

const command: JHipsterCommandDefinition = {
  options: {
    workspacesFolders: {
      type: Array,
      description: 'Folders to use as monorepository workspace',
      default: [],
      scope: 'generator',
    },
    monorepository: {
      type: Boolean,
      description: 'Use monorepository',
      scope: 'storage',
    },
    workspaces: {
      type: Boolean,
      description: 'Generate workspaces for multiples applications',
      scope: 'generator',
    },
    generateApplications: {
      type: Function,
      scope: 'generator',
      hide: true,
    },
    generateWith: {
      type: String,
      default: GENERATOR_APP,
      scope: 'generator',
      hide: true,
    },
  },
};

export default command;
