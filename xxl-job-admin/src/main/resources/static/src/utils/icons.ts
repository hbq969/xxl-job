import {defineComponent, h} from 'vue'
import {Icon} from '@iconify/vue'

// 把 Iconify（lucide）图标包成组件，供 el-icon / el-button :icon 使用；图标尺寸 1em 跟随字号。
const make = (name: string, icName: string) => defineComponent({
  name: icName,
  setup: () => () => h(Icon, {icon: `lucide:${name}`, width: '1em', height: '1em'})
})

export const IcArrowLeft = make('arrow-left', 'IcArrowLeft')
export const IcDelete = make('trash', 'IcDelete')
export const IcDocument = make('file-text', 'IcDocument')
export const IcDocumentCopy = make('copy', 'IcDocumentCopy')
export const IcEdit = make('pencil', 'IcEdit')
export const IcPlus = make('plus', 'IcPlus')
export const IcSetting = make('settings', 'IcSetting')
export const IcUploadFilled = make('upload', 'IcUploadFilled')
export const IcUserFilled = make('user', 'IcUserFilled')
export const IcVideoPlay = make('play', 'IcVideoPlay')

export const IcVideoPause = make('pause', 'IcVideoPause')
export const IcPrinter = make('printer', 'IcPrinter')
export const IcOperation = make('settings-2', 'IcOperation')
