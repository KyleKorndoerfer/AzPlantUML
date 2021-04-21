# AzPlantUML

Azure function that can be hosted in your own infrastructure to generate
[PlantUML](https://plantuml.com) diagrams from a source file in an Azure DevOps
git repo.

They say a picture is worth a thousand words, so having accurate and current
diagrams of your architecture can be very helpful. The problem with most diagrams
is the hasle involved in updating them as changes occur. Moving shapes and
re-connecting all of the arrows or rearranging everything so that it looks nice
can make is such a chore that it is quickly forgotten or abandonded.

While there are currently some online services you can use to generate your diagrams
from source, this means that your documentation (some of which might be considered
proprietary) is being sent to a 3rd party out of your control. By hosting a service
yourself, you can ensure the privacy of your information.

Azure Functions is a great fit since it has a consumption plan that provides a
generous amount of free compute usage. If your volume is low enough, you would only
need to pay for the storage account used to host the function (which is also cheap).

## Use Case

Storing the diagram source files in DevOps is a no brainer; changes to your diagrams
can be reviewed at the same time as your source code. This makes it easy to keep things
current.

Using either a DevOps Wiki article or markdown file in a repo, you can
create an image link ([more info here](https://docs.microsoft.com/en-us/azure/devops/project/wiki/markdown-guidance?view=azure-devops#images))
that points to the Azure Function with a parameter that includes points to the
PlantUML source file you want to generate an image for.

*Example:*  `![image description]({function URI}/api/diagram?repo={repo}&src={git/repo/path})`

where:

- `{function URI}` is the URI to the function app
- `repo` is the name of the git repository that contains the PlantUML file
- `{git/repo/path}` is the path to the PlantUML file within the specified

## Features

Since our primary use case is generating diagrams for use in Azure DevOps Wiki
articles or Markdown files, that is what we will be striving for first. After we
get that working, we will most likely expand this to include support for GitHub
accounts.

Configuration parameters:

- `devopsUri` - This is the base path of your project.
	- For example `https://dev.azure.com/OrganizationFoo/ProjectBar`.
- `pat` - Personal Access Token. This is needed to use the Azure DevOps REST api to retrieve the diagram source from your git repo.

## Requirements

- Visual Studio Code
- Azure DevOps account
	- Anyone can create an account for [free here](https://azure.microsoft.com/en-us/services/devops/?nav=min)
- OpenJDK version of java
- Azure Functions runtime

Other developer environments will work (like IntelliJ or Exclipse), but this is
the setup that we use and know best.

### Setup

In order to work on this project, you will need the following:

1. Follow the '**Configure your environment**' sction of the *[Quickstart: Create a Java function in Azure using Visual Studio Code](https://docs.microsoft.com/en-us/azure/azure-functions/create-first-function-vs-code-java#configure-your-environment)* guide with the following exceptions:

	- Azure Functions use an OpenJDK build from [Azul Systems](https://www.azul.com/downloads/azure-only/zulu/) so I would highly recommend using that as your primary JDK to ensure everything works once deployed to Azure.
	- For long term support, we use Java 8 which is supported by Azul and Microsoft until 2030 but the Java tooling for Visual Studio Code requires Java 11. In order to make sure that everything works, install Zulu for Azure Java 11 first and then Zulu for Azure Java 8. This will ensure your PATH is configured properly for running the Azure Function locally.

## How to Build

- Clone the git repo to your local machine
- Open the root folder in Visual Studio Code
- Prees F5 to start debugging
