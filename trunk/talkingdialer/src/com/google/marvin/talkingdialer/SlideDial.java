// Copyright 2008 Google Inc. All Rights Reserved.

package com.google.marvin.talkingdialer;

import com.google.tts.TTS;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

/**
 * Enables the user to dial without looking at the phone.
 * 
 * The spot the user touches down is "5". What the user actually dials depends
 * on where they lift up relative to where they touched down; this is based on
 * the arrangement of numbers on a standard touchtone phone dialpad:
 * 
 * 1 2 3 4 5 6 7 8 9 * 0 #
 * 
 * Thus, sliding to the upperleft hand corner and lifting up will dial a "1".
 * 
 * A similar technique is used for dialing a contact. Stroking up will go to
 * previous contact; stroking down will go to the next contact.
 * 
 * @author clchen@google.com (Charles L. Chen)
 */
public class SlideDial extends Activity {

  private SlideDialView mView;
  private ContactsView contactsView;
  public TTS tts;

  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    // android.os.Debug.waitForDebugger();

    tts = new TTS(this, ttsInitListener);

  }

  private TTS.InitListener ttsInitListener = new TTS.InitListener() {
    public void onInit(int version) {
      String pkgName = "com.google.marvin.talkingdialer";

      tts.addSpeech("You are about to dial", pkgName, R.raw.you_are_about_to_dial);
      tts.addSpeech("Dialing mode", pkgName, R.raw.dialing_mode);
      tts.addSpeech("deleted", pkgName, R.raw.deleted);
      tts.addSpeech("Nothing to delete", pkgName, R.raw.nothing_to_delete);
      tts.addSpeech("Phonebook", pkgName, R.raw.phonebook);
      tts.addSpeech("home", pkgName, R.raw.home);
      tts.addSpeech("cell", pkgName, R.raw.cell);
      tts.addSpeech("work", pkgName, R.raw.work);
      tts.addSpeech("[honk]", pkgName, R.raw.honk);


      switchToDialingView();
    }
  };

  public void returnResults(String dialedNumber) {
    dialedNumber = dialedNumber.replaceAll("\\D+", "");
    Uri myData = Uri.parse(Uri.encode(dialedNumber));
    Intent dummyIntent = new Intent();
    dummyIntent.setData(myData);
    setResult(RESULT_OK, dummyIntent);
    finish();
  }

  public void switchToContactsView() {
    if (contactsView != null) {
      contactsView.setVisibility(View.GONE);
    }
    contactsView = new ContactsView(this);
    setContentView(contactsView);
    tts.speak("Phonebook", 0, null);
  }

  public void switchToDialingView() {
    if (mView != null) {
      mView.setVisibility(View.GONE);
    }
    mView = new SlideDialView(this);
    mView.parent = this;
    setContentView(mView);
    tts.speak("Dialing mode", 0, null);
  }


  public void quit() {
    setResult(RESULT_CANCELED, null);
    finish();
  }
  
  @Override
  public void onStop(){
    super.onStop();
    mView.unregisterListeners();
  }

  @Override
  protected void onDestroy() {
    tts.shutdown();
    super.onDestroy();
  }
}