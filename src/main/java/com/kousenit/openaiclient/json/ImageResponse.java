package com.kousenit.openaiclient.json;

import java.util.List;

public record ImageResponse(Long created,
                            List<Image> data) {
}
