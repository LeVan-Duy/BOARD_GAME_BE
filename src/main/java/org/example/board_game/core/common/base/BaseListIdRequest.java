package org.example.board_game.core.common.base;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class BaseListIdRequest {
    
    List<String> ids = new ArrayList<>();
}
