#!/bin/sh

export PATH=$PATH:/root/.pulumi/bin

# TODO: as a parameter
export DSL_SCRIPT=/github/workspace/script.infra.kts

pulumi login --local
pulumi preview --non-interactive
