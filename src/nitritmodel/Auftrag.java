package nitritmodel;

import data.PipeData;
import org.dizitart.no2.objects.Id;
import java.io.Serializable;

public class Auftrag extends PipeData<Auftrag> implements Serializable
{
    @Id
    private long mAuftragsId;
    private long mErstelltID;
    private long mAblaufUhrzeit;
    private int mColorCode;
    private int mPrio;
    private String mBeschreibung;
    private String mAufgabe;
    private ContactDTO contactDTO;
    private ViewType viewType;

    public long getmAuftragsId() {
        return mAuftragsId;
    }

    public void setmAuftragsId(long mAuftragsId) {
        this.mAuftragsId = mAuftragsId;
    }

    public long getmErstelltID() {
        return mErstelltID;
    }

    public void setmErstelltID(long mErstelltID) {
        this.mErstelltID = mErstelltID;
    }

    public long getmAblaufUhrzeit() {
        return mAblaufUhrzeit;
    }

    public void setmAblaufUhrzeit(long mAblaufUhrzeit) {
        this.mAblaufUhrzeit = mAblaufUhrzeit;
    }

    public int getmColorCode() {
        return mColorCode;
    }

    public void setmColorCode(int mColorCode) {
        this.mColorCode = mColorCode;
    }

    public int getmPrio() {
        return mPrio;
    }

    public void setmPrio(int mPrio) {
        this.mPrio = mPrio;
    }

    public String getmBeschreibung() {
        return mBeschreibung;
    }

    public void setmBeschreibung(String mBeschreibung) {
        this.mBeschreibung = mBeschreibung;
    }

    public String getmAufgabe() {
        return mAufgabe;
    }

    public void setmAufgabe(String mAufgabe) {
        this.mAufgabe = mAufgabe;
    }

    public ContactDTO getContactDTO() {
        return contactDTO;
    }

    public void setContactDTO(ContactDTO contactDTO) {
        this.contactDTO = contactDTO;
    }

    public ViewType getViewType() {
        return viewType;
    }

    public void setViewType(ViewType viewType) {
        this.viewType = viewType;
    }
}
