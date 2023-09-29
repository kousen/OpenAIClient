## OpenAI Spring Demo

This repository demonstrates how to call several [OpenAI services](https://platform.openai.com/docs/api-reference) from Spring without using the Spring AI project. Note that OpenAI requires an API key, which you must set as an environment variable, and you have to give them a credit card number to use the service. Fortunately, the cost for all the services (other than potentially GPT-4) is minimal.

## Prerequisites

* Set the `OPENAI_API_KEY` environment variable to access any of the OpenAI services. The value of that variable is autowired into the `WebClient` bean configured in the `AppConfig` class.

The default image size for DALL-E is specified in `application.properties`:

* `dalle.default_image_size=512x512`

You can adjust the default image size to 256x256 or 1024x1024.

## Running

The application does not have any controllers, since it just shows you how to add these services to existing Spring applications. You can run each service through an associated test:

* `OpenAIServiceTest` to list the available models and call ChatGPT
* `DallEServiceTest` for image generation with DALL-E 2
* `OpenAIInterfaceTest` to try out the HTTP exchange interfaces for ChatGPT and DALL-E 2. Note that this service is autowired into the `OpenAIServiceTest`.
* `TranscriptionServiceTest` to transcribe an audio file into text. This service uses the `WebClient` class directly, since it got too complicated to deal with the different file formats in the HTTP exchange interfaces.

## License
This project is licensed under the MIT License - see the [LICENSE.md](LICENSE) file for details

## Author

Ken Kousen

For more details, see _Tales from the jar side_
* [My YouTube Channel](https://www.youtube.com/@talesfromthejarside?sub_confirmation=1)
* [My free weekly newsetter](https://kenkousen.substack.com/)

