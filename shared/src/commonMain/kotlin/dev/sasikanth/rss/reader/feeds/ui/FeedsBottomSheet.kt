/*
 * Copyright 2023 Sasikanth Miriyampalli
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.sasikanth.rss.reader.feeds.ui

import androidx.compose.animation.core.Transition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import dev.sasikanth.rss.reader.components.AsyncImage
import dev.sasikanth.rss.reader.database.Feed
import dev.sasikanth.rss.reader.feeds.FeedsEffect
import dev.sasikanth.rss.reader.feeds.FeedsEvent
import dev.sasikanth.rss.reader.feeds.FeedsViewModel
import kotlinx.collections.immutable.ImmutableList

@Composable
internal fun FeedsBottomSheet(
  feedsViewModel: FeedsViewModel,
  bottomSheetSwipeTransition: Transition<Float>,
  closeSheet: () -> Unit
) {
  val state by feedsViewModel.state.collectAsState()
  val selectedFeed = state.selectedFeed

  LaunchedEffect(Unit) {
    feedsViewModel.effects.collect { effect ->
      when (effect) {
        FeedsEffect.MinimizeSheet -> closeSheet()
      }
    }
  }

  Column(modifier = Modifier.fillMaxSize()) {
    BottomSheetHandle(bottomSheetSwipeTransition)

    Box {
      BottomSheetCollapsedContent(
        modifier =
          Modifier.graphicsLayer { alpha = 1f - (bottomSheetSwipeTransition.currentState * 5f) },
        feeds = state.feeds,
        selectedFeed = selectedFeed,
        onFeedSelected = { feed -> feedsViewModel.dispatch(FeedsEvent.OnFeedSelected(feed)) }
      )
    }
  }
}

@Composable
private fun BottomSheetCollapsedContent(
  feeds: ImmutableList<Feed>,
  selectedFeed: Feed?,
  onFeedSelected: (Feed) -> Unit,
  modifier: Modifier = Modifier
) {
  LazyRow(
    modifier = modifier.padding(start = 100.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    contentPadding = PaddingValues(end = 24.dp)
  ) {
    items(feeds) { feed ->
      BottomSheetItem(
        text = feed.name.uppercase(),
        icon = {
          AsyncImage(
            url = feed.icon,
            contentDescription = null,
            modifier = Modifier.requiredSize(56.dp).clip(RoundedCornerShape(16.dp))
          )
        },
        selected = selectedFeed == feed,
        onClick = { onFeedSelected(feed) }
      )
    }
  }
}
