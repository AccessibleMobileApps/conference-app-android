package com.robinkanatzar.conference.di

import com.robinkanatzar.conference.ui.about.AboutFragment
import com.robinkanatzar.conference.ui.chooseconference.ChooseConferenceFragment
import com.robinkanatzar.conference.ui.contact.ContactFragment
import com.robinkanatzar.conference.ui.login.LoginFragment
import com.robinkanatzar.conference.ui.news.NewsFragment
import com.robinkanatzar.conference.ui.newsdetail.NewsDetailFragment
import com.robinkanatzar.conference.ui.partners.PartnersFragment
import com.robinkanatzar.conference.ui.partnersdetail.PartnersDetailFragment
import com.robinkanatzar.conference.ui.schedule.ScheduleFragment
import com.robinkanatzar.conference.ui.scheduledetail.ScheduleDetailFragment
import com.robinkanatzar.conference.ui.settings.SettingsFragment
import com.robinkanatzar.conference.ui.speakers.SpeakersFragment
import com.robinkanatzar.conference.ui.speakersdetail.SpeakersDetailFragment

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeAboutFragment(): AboutFragment

    @ContributesAndroidInjector
    abstract fun contributeContactFragment(): ContactFragment

    @ContributesAndroidInjector
    abstract fun contributeChooseConferenceFragment(): ChooseConferenceFragment

    @ContributesAndroidInjector
    abstract fun contributeLoginFragment(): LoginFragment

    @ContributesAndroidInjector
    abstract fun contributeNewsFragment(): NewsFragment

    @ContributesAndroidInjector
    abstract fun contributeNewsDetailFragment(): NewsDetailFragment

    @ContributesAndroidInjector
    abstract fun contributePartnersFragment(): PartnersFragment

    @ContributesAndroidInjector
    abstract fun contributePartnersDetailFragment(): PartnersDetailFragment

    @ContributesAndroidInjector
    abstract fun contributeScheduleFragment(): ScheduleFragment

    @ContributesAndroidInjector
    abstract fun contributeScheduleDetailFragment(): ScheduleDetailFragment

    @ContributesAndroidInjector
    abstract fun contributeSettingsFragment(): SettingsFragment

    @ContributesAndroidInjector
    abstract fun contributeSpeakersFragment(): SpeakersFragment

    @ContributesAndroidInjector
    abstract fun contributeSpeakersDetailFragment(): SpeakersDetailFragment
}
