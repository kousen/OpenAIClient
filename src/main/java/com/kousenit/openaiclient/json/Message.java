package com.kousenit.openaiclient.json;

import com.kousenit.openaiclient.util.Role;

public record Message(Role role, String content) {}