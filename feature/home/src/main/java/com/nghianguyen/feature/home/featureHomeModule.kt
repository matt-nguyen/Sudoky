package com.nghianguyen.feature.home

import com.nghianguyen.feature.home.viewmodel.HomeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val featureHomeModule = module { viewModel<HomeViewModel> { HomeViewModel(get()) } }
