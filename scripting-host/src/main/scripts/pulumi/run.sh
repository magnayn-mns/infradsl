#!/bin/sh

export PATH=$PATH:/root/.pulumi/bin

# TODO: as a parameter
export DSL_SCRIPT=/github/workspace/script.infra.kts
export PULUMI_CONFIG_PASSPHRASE=

pulumi login --local
pulumi stack select foo
pulumi preview --non-interactive
